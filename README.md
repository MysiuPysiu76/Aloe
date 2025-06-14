# 🌿 Aloe

**Aloe** is a simple and elegant file explorer built in **Java** using **JavaFX**.  
It features a clean and intuitive interface designed for ease of use, while offering a wide range of customization options to fit your personal workflow. Aloe is lightweight, fast, and user-friendly — perfect for both casual users and power users who appreciate simplicity without sacrificing flexibility.

## ✨ Features

- 📁 **Intuitive user interface** with easy navigation through the file system
- 🗂️ **Browse and manage files and folders** (copy, move, delete, rename, etc.)
- 🗜️ **Support for creating archives** in popular formats:
   - `.zip`, `.tar`, `.tar.gz`, `.jar`, `.7z` — directly from the interface
- 🧩 **Extensive context menu customization**
   - Add or remove actions, adjust behavior, and even configure custom shortcuts
- ⚙️ **Advanced personalization options**:
   - Customize the appearance, color scheme, behavior, and layout of the application
   - A rich settings panel lets you tailor the experience to your workflow
- 🌙 **Built-in Dark Mode** — automatic or manual switching
- 🖼️ **File metadata preview**:
  - Displays extended properties for **images** (e.g. resolution, type)
  - Shows technical details for **videos** (e.g. duration, resolution, codec)
- 🌐  **Multilingual support** — Aloe is available in multiple languages via dynamic translation files
- 🎨 **Style and color palette inspired by Nautilus and Cosmic Files**, with a focus on aesthetics and clarity

## 🚀 Requirements

- ☕ **Java JDK 23** or newer
- 🛠️ **Maven** — for building and running the project

### Step 1: Verify Maven Installation
Check if Maven is installed and accessible:
```bash
mvn -version
```

If Maven is not installed, please install it first. Refer to the [Maven installation guide](https://maven.apache.org/install.html) for details.

### Step 2: Verify Java Installation
Ensure you have JDK version **23 or higher** installed:
```bash
java --version
```

If your version is below 23, update to a compatible JDK. You can download the latest version from the [OpenJDK website](https://openjdk.org/) or other sources like [Adoptium](https://adoptium.net/).

### Step 3: Download the Project
Clone the repository and navigate into it:
```bash
git clone https://github.com/MysiuPysiu76/Aloe
cd Aloe
```

### Step 4: Build the Project
Clean, compile, and install the project artifact:
```bash
mvn clean install
```

### Step 5: Run the Application
Run the JavaFX application:
```bash
mvn javafx:run
```

## Icons
Icons are sourced from [flaticon.com](https://www.flaticon.com/).

## License Notice

This product uses FFmpeg under the terms of the GNU Lesser General Public License v2.1 or later
and GNU General Public License v2 or later.  
See [FFmpeg legal](https://www.ffmpeg.org/legal.html) for full license texts.
