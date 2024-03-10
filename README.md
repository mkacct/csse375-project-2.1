# Project: Linter

## Dependencies
Specified in pom.xml

## Contributors

### Original authors

Madeline Kahn, Nolan Cales, Kyle Asbury

### Current maintainers

Madeline Kahn, Ryan Shiraki

## Usage

`<command to run LinterProject> <classdir> [<config>]`

* `<command to run LinterProject>`: The call to `java` to run the `presentation.Main` class
* `<classdir>`: Path to the directory containing the `.class` files to be linted
* `[<config>]`: Optional path to a JSON configuration file

### Configuration

Global properties:

* `"skipUnmarkedChecks"` (boolean): If true, all checks only run if they are explicitly enabled. Otherwise, most checks (those that are not disabled by default) run unless they are explicitly disabled. (Use the wiki to see which checks are enabled or disabled by default.)
* `"enable_<CHECK>"` (boolean): Whether to run &lt;CHECK&gt; (where &lt;CHECK&gt; is the name of a check).

Properties used by specific checks shall be documented on the check's wiki page.
