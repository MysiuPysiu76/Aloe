# Aloe
Aloe is a simple file explorer written in Java using JavaFX, inspired by the Nautilus file manager in Pop!_OS 22.04.

## Usage
**Requirements**:
- Installed Maven.
- Installed Java JDK (version **22 or higher**), with the `JAVA_HOME` environment variable set.

### Step 1: Verify Maven Installation
Check if Maven is installed and accessible:
```bash
mvn -version
```

If Maven is not installed, please install it first. Refer to the [Maven installation guide](https://maven.apache.org/install.html) for details.

### Step 2: Verify Java Installation
Ensure you have JDK version **22 or higher** installed:
```bash
java --version
```

If your version is below 22, update to a compatible JDK. You can download the latest version from the [OpenJDK website](https://openjdk.org/) or other sources like [Adoptium](https://adoptium.net/).

### Step 3: Set the `JAVA_HOME` Environment Variable
If `JAVA_HOME` is not already set, configure it as follows:

#### On Linux/Mac:
1. Locate your JDK installation path (e.g., `/usr/lib/jvm/java-22-openjdk`).
2. Export the variable in your terminal:
   ```bash
   export JAVA_HOME=/path/to/your/jdk
   ```
3. To make this change permanent, add the export command to your `~/.bashrc` or `~/.zshrc` file:
   ```bash
   echo 'export JAVA_HOME=/path/to/your/jdk' >> ~/.bashrc
   source ~/.bashrc
   ```

#### On Windows:
1. Find your JDK installation directory (e.g., `C:\Program Files\Java\jdk-22`).
2. Open the Start menu and search for "Environment Variables."
3. Add a new variable:
    - **Variable Name**: `JAVA_HOME`
    - **Variable Value**: `C:\Path\To\Your\JDK`

### Step 4: Download the Project
Clone the repository and navigate into it:
```bash
git clone git@github.com:MysiuPysiu76/Aloe.git
cd Aloe
```

### Step 5: Build the Project
Clean, compile, and install the project artifact:
```bash
mvn clean install
```

### Step 6: Run the Application
Run the JavaFX application:
```bash
mvn javafx:run
```

## Icons
Icons are sourced from [flaticon.com](https://www.flaticon.com/).
