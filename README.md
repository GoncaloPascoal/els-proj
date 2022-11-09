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
- `columns` (optional): List containing the names of columns to save, in the desired order.

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

---

## Checkpoint 2

In this section, we comprehensively describe the changes to our application since the first checkpoint.

### Changes to the Semantic Model

Since the first checkpoint, we have introduced support for extracting and manipulating multiple tables within the program, created new instructions and changed certain parameters for some existing instructions and added support for more file types. We describe these changes in more depth in the sections below.

#### Instructions

- Implemented a new instruction, `merge`, which will perform a merge operation on two or more tables
  - In our application, this is a column-wise join. We were inspired by the Python library `pandas` when selecting this naming convention, which provides `merge` and `concat` functions for its dataframes.
- Added `source` or `target` parameters to all instructions to allow users to specify the identifiers for the tables they wish to manipulate, as the current version of the program allows for multiple tables (see Program State section).
- Split the `load` instruction into the `loadStructured` and `loadUnstructured` instructions. Both still inherit certain parameters from the `load` base instruction, such as the files to read or the metadata columns to include.
  - `loadStructured` is best used to extract tables from files with a tree-like structure, such as JSON, XML or YAML (at this point, we support both JSON and XML).
  - `loadUnstructured` can be used with text files with an arbitrary structure, such as `.txt` files. To allow users to extract tabular data from these files, we also defined several text instructions, detailed in the section below.
- Added support for **glob expressions** when specifying input files for `load` instructions. Instead of having to individually specify each file location, we can now simply use a pattern such as `vitis_report_*.xml` or `dir/*.json`.
- Added a new metadata type, `directory`, which corresponds to the name of the directory where a given input file resides.
- Replaced the key parameter in `loadStructured` instructions with a path-like specification based on **XPath**, which allows for more expressiveness when selecting a certain node in the tree. Note that our implementation for JSON files does not support all of the XPath features. Some examples:
  - `/` will select the root object
  - `//Resources` will select the first `Resources` object anywhere in the tree
  - `/Resources` will select the `Resources` object that is a direct child of the root object
  - `//AreaEstimates/Resources` will select the `Resources` object that is a direct child of an `AreaEstimates` object which may be anywhere in the tree
- We also allow the specification of multiple paths within a single `loadStructured` instruction, which is relevant to the main use case for checkpoint 2.

#### Text Instructions

- In order to cope with the difficulty of extracting tables from an unstructured file, we implemented different **text instructions**, which have a similar interface to the top-level instructions but are specifically designed to extract tabular data from unstructured text files.
- A list of these **text instructions** is used as one of the parameters of the `loadUnstructured` instruction.
- The `columnInterval` instruction extracts tabular data from a file.
  - When extracting simultaneously from multiple lines, the values in the columns need to be aligned.
- The `regexLineDelimiter` instruction extracts data from a file in a key-value fashion.
  - Firstly, it selects the file lines which match one of the RegEx expressions in the `lines` parameter.
  - Afterwards, each of these lines is split based on the RegEx expression in the `delimiter` parameter, in key (to the left) and value (to the right). This expression is the same for all lines.

#### Program State

In order to support multiple tables, we introduced a `ProgramState` class, which currently only contains a `Map<String, Table>`, that is, a mapping of table identifiers to table objects. This state is passed as an argument to the `execute` method of the `Instruction` interface. We chose not to implement it as a singleton to allow more flexibility later in development (for example, we could execute several configuration files in parallel).

### Adapters

Mirroring the changes made to the `load` instruction, adapters are divided into two groups:

- `StructuredAdapter` - abstract base class for parsing structured files.
  - `XmlAdapter` - XML parsing and XPath functionality are provided by the `javax` library.
  - `JsonAdapter` - JSON parsing is provided by the `GSON` library. Support for a subset of XPath expressions was also implemented.
    - To implement XPath for JSON files, we first split the XPath string into a list of instances of the `PathFragment` class, containing the key for the node we are looking for and whether or not that key has to be a direct child of the parent node (true if the name of the key is preceded by a single slash).
    - Afterwards, we use a recursive function (`findByPath`) to locate an element corresponding to a given sequence of `PathFragment` instances (once the last `PathFragment` is found, we reach the base case of the recursive function). If such an element could not be found, the function returns `null`.
- `UnstructuredAdapter` - base class for parsing unstructured files.

### YAML Documentation (Changes)

#### Load Instruction - `load` (**REMOVED**)

Converted to a base class with the following parameters:

- `target` (**NEW**): identifier of the table to store the result of the file extraction.
- `files` (**CHANGED**): can now specify a list of glob expressions rather than individual paths to files.
- `metadataColumns` (**CHANGED**): implemented a new metadata type (`directory`).

#### Load Structured Instruction - `loadStructured` (**NEW**)

Extracts a table from a file or set of files with a tree-like structure. Inherits the `load` instruction's parameters.

- `key` (**REMOVED**)
- `paths` (**NEW**): list of XPath strings corresponding to nodes containing the columns we wish to extract. Note that only columns containing primitive data will be extracted (columns containing lists or maps are excluded).
- `columns` (unchanged)

#### Load Unstructured Instruction - `loadUnstructured` (**NEW**)

Extracts a table from a file or set of unstructuredfiles. Inherits the `load` instruction's parameters.

- `instructions`: list of text instructions that specify how to extract the table.
  - **Possible Instructions:**
  - `columnInterval`:
    - `lines`: list of file lines from which to extract the columns. The first line of a file is line 1.
    - `columnIntervals`: column names and corresponding position in the extracted line. The first column of a line is column 1.
    - `stripWhitespace` (optional): whether or not the whitespace surrounding the extracted information should be removed. Defaults to `true`.
  - `regexLineDelimiter`:
    - `linePatterns`: RegEx patterns corresponding to the beginning of the lines containing the data to extract.
    - `delimiter`: RegEx pattern representing the separation between the name and the value of the column.

#### Merge Instruction - `merge` (**NEW**)

Merges two or more tables (with a column-wise join), storing the result in a specified table.

- `tables`: identifiers of the tables to merge.
- `target` (optional): identifier of the table where the result will be stored. Can correspond to a table that does not exist, in which case the table will be created. If omitted, will correspond to the first identifier in the `tables` list.

#### Rename Instruction - `rename` (**CHANGED**)

- `source` (**NEW**): identifier of the table whose columns we wish to rename.

#### Save Instruction - `save` (**CHANGED**)

- `source` (**NEW**): identifier of the table to save.

### Example YAML Configuration File (Checkpoint 2)

```yaml
- loadStructured:
    target: t1
    files:
      - test/res/checkpoint2/data/vitis-report.xml
    path: [//AreaEstimates/Resources]
    metadataColumns:
      Folder: directory
- loadStructured:
    target: t2
    files:
      - test/res/checkpoint2/data/decision_tree.json
    path: [/, /params]
- loadUnstructured:
    target: t3
    files:
      - test/res/checkpoint2/data/gprof.txt
    instructions:
      - columnInterval:
          lines: [6]
          columnIntervals:
            HighestPercentage: [1, 7]
            HighestName: 55
- merge:
    tables: [t1, t2, t3]
- save:
    source: t1
    file: out/base.csv
```

### Internal DSL

- Our internal DSL is mostly based on the Builder pattern and the fluent API design philosophy.
- By employing the `ProgramBuilder` class, we can specify programs using a similar structure to the YAML configuration file seen in the previous section. Builders for specific instructions also have a `close` method that allows us to terminate the specification of the current instruction and return to the parent builder. This allows us to specify the entire program using a single method chain.
- We included overloaded methods for certain instruction parameters so that the API consumer can specify arguments in a more convenient manner. For instance, the `withPaths` method of `LoadStructuredBuilder` can accept a `List<String>` or variadic arguments (`String...`).
- Since `LoadStructuredInstruction` and `LoadUnstructuredInstruction` both inherit from the `LoadInstruction` class, the corresponding builders use template arguments to ensure that we always have access to both superclass and subclass methods:
  ```java
  class LoadBuilder<T extends LoadBuilder<T>> extends InstructionBuilder {}
  class LoadStructuredBuilder extends LoadBuilder<LoadStructuredBuilder> {}
  ```
- Upon creation of the program, the builder classes only validate whether or not the required arguments were supplied to each instruction (that is, they check if these arguments are not null). The semantic validation (for example, if the list of files for a load instruction is not empty) is performed by the constructors of the specific instructions.

```java
ProgramBuilder builder = new ProgramBuilder();

builder
  .loadStructured()
    .withTarget("t1")
    .withFilePaths("test/res/checkpoint2/data/vitis-report.xml")
    .withPaths("//AreaEstimates/Resources")
    .withMetadataColumns(Map.of("Folder", MetadataType.DIRECTORY))
    .close()
  .loadStructured()
    .withTarget("t2")
    .withFilePaths("test/res/checkpoint2/data/decision_tree.json")
    .withPaths("/", "/params")
    .close()
  .loadUnstructured()
    .withTarget("t3")
    .withFilePaths("test/res/checkpoint2/data/gprof.txt")
    .columnInterval()
      .withLines(new Interval(6))
      .withColumnIntervals(Map.of(
        "HighestPercentage", new Interval(1, 7),
        "HighestName", new Interval(55))
      )
      .close()
    .close()
  .merge()
    .withTables("t1", "t2", "t3")
    .close()
  .save()
    .withSource("t1")
    .withPath("out/base.csv");

Program program = builder.create();
program.execute();
```
