# ELS Project

For this project, you need to [install Gradle](https://gradle.org/install/)

## Project Setup

Copy your source files to the ``src`` folder, and your JUnit test files to the ``test`` folder.

## Compile and Running

To compile and install the program, run ``gradle installDist``. This will compile your classes and create a launcher script in the folder ``build/install/els2022-g1/bin``. For convenience, there are two script files, one for Windows (``els2022-g1.bat``) and another for Linux (``els2022-g1``), in the root of the repository, that call these scripts.

After compilation, tests will be automatically executed, if any test fails, the build stops. If you want to ignore the tests and build the program even if some tests fail, execute Gradle with flags "-x test".

When creating a Java executable, it is necessary to specify which class that contains a ``main()`` method should be entry point of the application. This can be configured in the Gradle script with the property ``mainClassName``, which by default has the value ``pt.up.fe.els2022.Main``.

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening ``build/reports/tests/test/index.html``.

---

## Checkpoint 1

At the time of this checkpoint, our project contains the following components / features:

- An initial version of the **semantic model**, consisting of an internal table representation (`Table`) and an `Instruction` interface with several concrete implementations, which correspond to operations that can be performed on files / tables (extracting tables, transforming tables, storing tables).
- The ability to extract tables from files (and optionally include columns containing different types of file metadata) through the abstract `Adapter` class (currently, there is only a concrete implementation for XML files).
- An initial version of our DSL in the form of a YAML configuration file, which is parsed by the `ConfigParser` class and converted to instructions for our semantic model.

### Semantic Model

#### Table Representation

Internally, a table is represented as a map of column names to columns (strings to lists of strings). We used a `ListOrderedMap` from the Apache Commons library, since it maintains a list of keys in insertion order. This means that operations on columns are very efficient, at the detriment of operations on rows. Entries are currently stored as strings regardless of their content, but as new requirements are introduced we may choose to implement a type system for table columns.

The API currently exposes methods for adding or getting rows and columns, renaming columns and merging or concatenating tables. When applying these operations (or other insertion operations), missing entries will use the value `null`.
- The **merge** operation is similar to a join in SQL (only without specifying one or more columns to join by), where the resulting table contains the columns of both input tables. If a column exists in both tables, this operation will fail.
- The **concatenate** operation will simply append the rows of the second table to the bottom of the first table.

To prevent API consumers from directly mutating a table's internal state without using the public API, any get methods (`getColumn` / `getRow`) return unmodifiable views.

#### Instructions
Our program currently supports three types of instructions: load instructions, rename instructions and save instructions, which all implement the `execute()` method from the `Instruction` interface. These instructions detail the modifications that can be applied to the data and represent the way to express the desired process.

Our program first creates a set of instructions by parsing a YAML configuration file and then executes them sequentially. When an invalid argument is passed to an instruction or something goes wrong at runtime (for example, the user attempted to use a column name that already exists), the program will halt with an error. We considered this behaviour to be preferable to skipping the malformed instruction, as that could cause unpredictable behaviour later in the execution.

### YAML Documentation

YAML configuration files (in the first checkpoint) take the form of a list of instructions, each having a specific set of parameters. Below is a list of all valid instructions and parameters.

#### Load Instruction (`load`)

Extracts a table from a file or set of files. Additional columns containing metadata about the files can also be included in the table.

- `files`: List containing paths to (XML) files containing the data to extract.
- `key`: Section of the document where the data to extract is located (in XML files, the program will look for the first element whose tag matches this string).
- `columns` (optional): List of strings containing the names of the columns to extract. When unspecified, all elements within the chosen section will be extracted.
- `metadataColumns` (optional): Map of strings to strings, where keys denote a column name and values denote the type of file metadata to store in that column. Metadata columns always appear at the beginning of the table. Valid metadata types are:
  - `fileName`
  - `filePath`
  - `absoluteFilePath`

#### Rename Instruction (`rename`)

Renames a column or set of columns.

- `mapping`: Map of strings to strings, where the keys denote the old column names and the values denote the new column names. Note that an error will occur when trying to use an already existing column name.

#### Save Instruction (`save`)

Saves table as a CSV file, optionally filtering and reordering the columns to save.

- `file`: Path to the file where the table will be saved. The file and its parent directories will be created if they do not exist.
- `columns`: List containing the names of columns to save, in the desired order.

### Example YAML Configuration File

```yaml
- load:
    files:
      - test/res/checkpoint1/data/vitis-report_1.xml
      - test/res/checkpoint1/data/vitis-report_2.xml
      - test/res/checkpoint1/data/vitis-report_3.xml
    key: Resources
    columns: [LUT, FF, DSP48E, BRAM_18K]
    metadataColumns:
      File: fileName
- rename:
    mapping:
      BRAM_18K: BRAMs
      DSP48E: DSPs
      LUT: LUTs
      FF: FFs
- save:
    file: out/base.csv
```

### Configuration Files

We chose to use the YAML language for our configuration files due to its readability and simplicity. We wanted to minimize the amount of "boilerplate" in our configuration files, so that the user can clearly express their intent in as few lines as possible. The following bullet points explain some of our design decisions regarding the configuration files. Most of them are ideas that we also intend to apply to the design of our DSL.

- Using a YAML object key to specify the type of instruction to avoid having a repetitive `instruction: type` key-value pair in each instruction
- Specifying metadata columns in the `load` instruction using a mapping of column names to the desired metadata type provides the most flexibility compared to a simple boolean or list of metadata types to include (avoids future rename operations or name conflicts with data columns)
- Using a map of strings to strings for the `rename` instruction felt very natural, and wrapping it under the `mapping` parameter ensures that we are able to add additional parameters to the rename instruction without worrying about possible conflicts with column names
- When the `columns` parameter is not specified in the `load` or `save` instructions, by default, all available columns will be extracted from / written to the chosen file.
