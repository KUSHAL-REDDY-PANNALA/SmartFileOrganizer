# Smart File Organizer & Duplicate Detector

An advanced file system analysis tool engineered using complex Data Structures and Algorithms in Java.

## Under The Hood (DSA Used)
- **HashMaps & SHA-256 Cryptography**: Maps hashed byte streams to absolute file paths for 100% accurate duplicate detection without relying on filenames.
- **HashSets**: Tracks evaluated directory paths to completely prevent infinite cycles caused by deep symbolic links.
- **Tree Structures**: Custom `FileNode` implementations dynamically build the directory hierarchy.
- **Priority Queue (Min-Heap)**: Maintains a rolling window of the top largest files in memory with $O(\log k)$ insertions.
- **Sorting Algorithms**: Custom runtime comparators invert heap arrays to output descending size logs.

## Tech Stack
- **Backend:** Java 11+, Core `java.nio.file` APIs, Lightweight `HttpServer`.
- **Database:** SQLite (JDBC) for persisting hashed indices.
- **Frontend:** HTML/CSS/Vanilla JS Fetch API Dashboard.

## How to Run
1. Ensure you have [Java JDK 11+](https://adoptium.net/) and [Maven](https://maven.apache.org/) installed.
2. Clone this repository.
3. Open a terminal in the root directory and build the project:
   ```bash
   mvn clean install