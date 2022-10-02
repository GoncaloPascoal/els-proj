
# Configuration Documentation

YAML configuration files (in the first checkpoint) take the form of a list of instructions, each having a specific set of parameters. Below is a list of all valid instructions and parameters.

## Load Instruction (`load`)

Extracts a table from a file or set of files. Additional columns containing metadata about the files can also be included in the table.

### Parameters

- `files`: List containing paths to (XML) files containing the data to extract.
- `key`: Section of the document where the data to extract is located (in XML files, the program will look for the first element whose tag matches this string).
- `columns` (optional): List of strings containing the names of the columns to extract. When unspecified, all elements within the chosen section will be extracted.
- `metadataColumns` (optional): Map of strings to strings, where keys denotes a column name and values denote the type of file metadata to store in that column. Metadata columns always appear at the beginning of the table. Valid metadata types are:
  - `fileName`
  - `filePath`
  - `absoluteFilePath`

## Rename Instruction (`rename`)

Renames a column or set of columns.

### Parameters

- `mapping`: Map of strings to strings, where the keys denote the old column names and the values denote the new column names. Note that an error will occur if trying to use an already existing column name.

## Save Instruction (`save`)

Saves table as a csv file, optionally filtering and reordering the columns to save.

- `file`: Path to the file where the table will be saved. The file and its parent directories will be created if they do not exist.
- `columns`: List containing the names of columns to save, in the desired order.
