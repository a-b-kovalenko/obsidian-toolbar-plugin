# Open in Obsidian

IntelliJ IDEA plugin that adds a toolbar button to open the matching [Obsidian](https://obsidian.md) note for the currently open file.

## How it works

If your project root contains an `.obsidian` folder, it is treated as an Obsidian vault. The toolbar button finds the `.md` file in the vault whose name matches the file currently open in the editor and opens it in Obsidian. If no matching note is found, opens the vault instead.

The button is disabled when the current project is not an Obsidian vault or no file is open in the editor.

## Installation

Install from [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/com.akovalenko.obsidian-toolbar) or manually:

1. Download the latest `.zip` from [Releases](https://github.com/a-b-kovalenko/obsidian-toolbar-plugin/releases)
2. In IntelliJ IDEA: **Settings → Plugins → ⚙ → Install Plugin from Disk**

## Requirements

- IntelliJ IDEA 2025.1 or later
- macOS (uses `open` command to launch Obsidian)
- An Obsidian vault that is also an IntelliJ project (project root contains `.obsidian/`)

## Helper script

On first use, the plugin offers to install a helper script (`open-obsidian-vault`) to `~/bin/` for command-line use. The script is not removed automatically when the plugin is uninstalled — delete it manually if needed.

## Building from source

Requires JDK 21.

```bash
./gradlew build          # compile + test
./gradlew runIde         # launch sandbox IDE with plugin installed
./gradlew buildPlugin    # produce distributable .zip in build/distributions/
```

## License

[MIT](LICENSE)
