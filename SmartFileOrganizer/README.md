Smart File Organizer & Duplicate Detector
An advanced file-system analysis tool engineered in Java, demonstrating practical applications of complex Data Structures and Algorithms (DSA) for large-scale file analysis, duplicate detection, and storage optimization.
Under the Hood — DSA Used
HashMaps & SHA-256 Cryptography: Maps cryptographic hashes of file contents to absolute file paths, enabling accurate duplicate detection without relying on filenames.
HashSets: Tracks evaluated directory paths to prevent infinite traversal cycles caused by symbolic links.
Tree Structures: Custom FileNode implementations dynamically represent the directory hierarchy.
Priority Queue (Min-Heap): Maintains a rolling collection of the top k largest files with O(log k) insertion complexity.
Sorting Algorithms: Custom comparators process the heap results to produce descending file-size reports.
Tech Stack
Backend
Java 11+
java.nio.file APIs
Lightweight Java HttpServer
Database
SQLite
JDBC
Frontend
HTML
CSS
Vanilla JavaScript
Fetch API
How to Run
Prerequisites
Ensure the following are installed:
Java JDK 11+
Maven 3.8+
Git
1. Clone the Repository
git clone <your-repository-url>
cd <project-directory>

2. Build the Project
Open a terminal in the root directory and run:
mvn clean install

This command cleans previous build files, compiles the project, runs the tests, and installs the generated artifact into your local Maven repository.
3. Run the Application
Start the Smart File Organizer using:
mvn exec:java -Dexec.mainClass="com.organizer.App"

The application will start the backend server and initialize the file-system analysis service.
4. Open the Dashboard
Once the application starts successfully, open your browser and navigate to the URL displayed in the terminal.
For example:

http://localhost:8080

You can then use the web dashboard to scan directories, identify duplicate files, analyze storage usage, and view the largest files.
Core Features
Recursive directory scanning
SHA-256 based duplicate-file detection
Symbolic-link cycle protection
Directory-tree visualization
Top-k largest-file analysis
Persistent SQLite hash index
Browser-based dashboard
REST-style HTTP endpoints for frontend communication
Complexity Overview
Operation	Data Structure / Algorithm	Complexity
File hash lookup	HashMap	O(1) average
Visited-directory check	HashSet	O(1) average
Directory hierarchy insertion	Tree	O(d)
Top-k file insertion	Min-Heap	O(log k)
Top-k extraction	Heap + sorting	O(k log k)
File traversal	NIO filesystem traversal	O(n) files/directories

Where n is the number of filesystem entries, k is the requested number of largest files, and d is the depth of the directory hierarchy.
Project Structure
src/
├── main/
│   ├── java/
│   │   └── ...
│   └── resources/
│       └── ...
├── test/
│   └── java/
│       └── ...
├── pom.xml
└── README.md

Example Use Cases
Find duplicate files consuming unnecessary disk space.
Identify the largest files in a directory.
Analyze complex directory structures.
Visualize filesystem hierarchies.
Build a persistent index for repeated filesystem scans.
Demonstrate real-world applications of DSA in Java.
Future Improvements
File deletion/recovery workflows
Configurable duplicate-detection algorithms
Parallel filesystem scanning
Incremental hashing for large files
Advanced storage analytics
Export reports as CSV/JSON
Improved dashboard visualizations
