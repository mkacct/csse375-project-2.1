# Project: Linter

## Dependencies
Specified in pom.xml

## Contributors
Madeline Kahn, Nolan Cales, Kyle Asbury

## Team Member's Engineering Notebooks (one per person)
- Madeline Kahn: https://rosehulman.sharepoint.com/:w:/s/GrpCSSE374S2Team05/EXwbCk60sk9CoKpcOLIr8WUBLEtOJZcmxGJYRdQuGfPB0A?e=CvdqXj
- Nolan Cales: https://rosehulman-my.sharepoint.com/:w:/g/personal/calesnm_rose-hulman_edu/ET2gjrVe5GVEtRich0NTHAIBt5ltUiNJJNkRClqD7VmwDw?e=jrwSiW
- Kyle Asbury: https://rosehulman.sharepoint.com/:w:/s/GrpCSSE374S2Team05/EZoAc_didcNEsTTb12zkYFABEH_oIMtvJ_a9v3wqfkZUXw?e=VopIoy

## Features

| Developer       | Style Check         | Principle Check                          | Pattern Check | A Feature (optional) |
|:----------------|:--------------------|:-----------------------------------------|:--------------|:---------------------|
| Madeline Kahn   | Length limits       | Program to interface, not implementation | Adapter       | JSON configuration   |
| Nolan Cales     | Unused abstractions | Information hiding                       | Strategy      | I/O reccomendations  |
| Kyle Asbury     | Naming conventions  | Low coupling                             | Observer      | PlantUML generation  |

## Usage

`<command to run LinterProject> <classdir> [<config>]`

* `<command to run LinterProject>`: The call to `java` to run the `presentation.Main` class
* `<classdir>`: Path to the directory containing the `.class` files to be linted
* `[<config>]`: Optional path to a JSON configuration file

### Configuration

Global properties:

* `"skipUnmarkedChecks"` (boolean): If true, checks not explicitly enabled will be skipped. Otherwise, checks not explicitly disabled will be run.
* `"enable_<CHECK>"` (boolean): Whether to run &lt;CHECK&gt; (where &lt;CHECK&gt; is the name of a check).

Properties used by specific checks shall be documented on the check's wiki page.

## Demo video

_TODO: Add link to demo video_
