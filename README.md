# librarydb-app

## Setup: using only Eclipse
- With Eclipse open, go to File -> Import
- Select "Git" -> "Project from Git (with smart import)"
- Select Clone URI
- Paste `https://github.com/wdelo/librarydb-app` into "URI:" and select "Next >"
- If all branches are not selected press "Select All", then select "Next >"
- Place the location of the project where you want (I recommend just inside the Eclipse workspace), then select "Next >"
- Make sure "Import source:" is the same location as the location in the previous step
- Make sure "Detect and configure project natures" is checked and press Finished

## Setup: using command line alongside Eclipse

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