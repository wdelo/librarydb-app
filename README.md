# librarydb-app

## Setup

### Cloning the project
- On the command line, go to the directory where you want the root directory of the repository to reside (I recommend going to your Eclipse workspace)
- Clone the repo into the directory: `git clone https://github.com/wdelo/librarydb-app.git`

### Importing to Eclipse
- With Eclipse open, go to File -> Import
- Select "Projects from Folder or Archive"
- Press the "Directory..." button and select the repository directory
- Make sure "Detect and configure project natures" is checked and press Finish

### Adding SQLite JDBC library
- Add the .jar file, , to the eclipse project
- Right-click on the project name
- Go to "Build Path" -> "Add External Archives..."
- In the project directory, select the .jar file, sqlite-jdbc-3.32.3.2.jar