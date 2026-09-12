package com.organizer.core;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScannerService {
    // DSA 1: HashMap for Duplicate Detection (Hash -> List of Paths)
    private final Map<String, List<String>> fileHashes = new ConcurrentHashMap<>();
    
    // DSA 2: HashSet to prevent infinite loops in symlinks
    private final Set<String> visitedPaths = Collections.synchronizedSet(new HashSet<>());
    
    // DSA 3: PriorityQueue (Min-Heap manipulated to act as Max-Heap) for top largest files
    private final PriorityQueue<FileNode> largestFiles = new PriorityQueue<>(
            Comparator.comparingLong(f -> f.size) // Min-Heap logic, we keep size at 10
    );

    // DSA 4: HashMap for Grouping by extension
    private final Map<String, Long> extensionStats = new HashMap<>();

    private Connection dbConnection;

    public ScannerService() {
        initDatabase();
    }

    private void initDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:file_data.db");
            Statement stmt = dbConnection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY, path TEXT, size REAL, hash TEXT)");
            stmt.execute("DELETE FROM files"); // Clear previous runs
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DSA 5: Trees (Returning the Root node of the hierarchy)
    public FileNode scanDirectory(String rootPath) throws Exception {
        Path start = Paths.get(rootPath);
        FileNode rootNode = new FileNode(start.getFileName().toString(), start.toString(), true, 0);
        
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (!visitedPaths.add(dir.toAbsolutePath().toString())) {
                    return FileVisitResult.SKIP_SUBTREE; // Avoid circular symlinks
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                processFile(file, attrs);
                return FileVisitResult.CONTINUE;
            }
        });

        saveToDatabase();
        return rootNode;
    }

    private void processFile(Path file, BasicFileAttributes attrs) {
        try {
            long size = attrs.size();
            String pathStr = file.toAbsolutePath().toString();
            String ext = getFileExtension(file.getFileName().toString());
            
            // Complex Hash generation for exact duplication
            String fileHash = computeSHA256(file);
            fileHashes.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(pathStr);

            extensionStats.put(ext, extensionStats.getOrDefault(ext, 0L) + size);

            FileNode node = new FileNode(file.getFileName().toString(), pathStr, false, size);
            
            synchronized (largestFiles) {
                largestFiles.offer(node);
                if (largestFiles.size() > 10) {
                    largestFiles.poll(); // Remove smallest of the top 10
                }
            }
        } catch (Exception ignored) {}
    }

    private void saveToDatabase() {
        try {
            PreparedStatement pstmt = dbConnection.prepareStatement("INSERT INTO files (path, size, hash) VALUES (?, ?, ?)");
            for (Map.Entry<String, List<String>> entry : fileHashes.entrySet()) {
                for (String path : entry.getValue()) {
                    pstmt.setString(1, path);
                    pstmt.setLong(2, 0); // Simplified for DB write speed
                    pstmt.setString(3, entry.getKey());
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String computeSHA256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) return "unknown";
        return name.substring(lastIndexOf + 1).toLowerCase();
    }

    public Map<String, Object> getResults() {
        Map<String, Object> results = new HashMap<>();
        
        // Sorting Algorithm applied on PriorityQueue to get descending order
        List<FileNode> topFiles = new ArrayList<>(largestFiles);
        topFiles.sort((a, b) -> Long.compare(b.size, a.size)); 

        Map<String, List<String>> duplicates = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : fileHashes.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }

        results.put("topLargestFiles", topFiles);
        results.put("duplicates", duplicates);
        results.put("extensionStats", extensionStats);
        return results;
    }
}