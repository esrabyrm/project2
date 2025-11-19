Address Statistics (CSV/XML, Java 17)

A small console application that reads address data from CSV or XML, normalizes fields, detects duplicate addresses by key (city|street|house|floors), and prints **per-city floor distributions (1–5 floors)*. The app runs in a loop until the user types q.

Features

CSV and XML input (UTF-8).

Normalization of string fields (trim, lower-case, collapse multiple spaces).

Multilingual header/tag aliases (ru/tr/en) for: city, street, house, floors.

Duplicate detection and per-city floor histogram (1F…5F).

Robust error handling (missing file, unsupported extension, parse/I/O errors).

Windows console friendly (UTF-8): chcp 65001, JVM -Dfile.encoding=UTF-8.

Requirements

Java JDK 17+

Windows console set to UTF-8 (see Run)

Project Structure
src/
  App.java
  ConsoleUI.java
  Address.java
  AddressReader.java
  CsvAddressReader.java
  XmlAddressReader.java
  Stats.java
  StatsService.java
docs/
  sample.csv
  uml_odev2.png
  report_odev.pdf
compile.bat
run.bat
README.md
Note: the out/ folder contains build artifacts and should be ignored by Git.

Input Formats
CSV
UTF-8, first line is header. Supported header aliases (any case):

city / sehir / şehir / город / gorod
street / cadde / улица
house / bina / дом
floors / kat / этажей
Quoted cells and escaped quotes ("") are handled.
Example: see docs/sample.csv

XML
SAX parsing, UTF-8. Tag aliases same as CSV (above). Records can be wrapped as <record> or <addr>.
Minimal example:
<addresses>
  <record>
    <city>Kazan</city>
    <street>Lenina</street>
    <house>10</house>
    <floors>5</floors>
  </record>
</addresses>

Build

Option A — scripts
compile.bat

Option B — manual
javac -encoding UTF-8 -d out src\*.java

Run

Option A — script
run.bat

Option B — manual (Windows, UTF-8)
chcp 65001
java -Dfile.encoding=UTF-8 -cp "out" App


Sample Session
File path (or q to quit): docs\sample.csv

Duplicates (count >= 2):
  kazan|lenina|10|5 -> 2

Floors per city:
  Naberezhnye : 1F=0  2F=0  3F=1  4F=0  5F=0
  Kazan       : 1F=1  2F=0  3F=0  4F=0  5F=2

Processed: 4 rows, skipped: 0
Processing time: 23 ms

File path (or q to quit):


Error Handling (examples)
Missing file: File not found.
Unsupported extension: Unsupported file type (use .csv or .xml).
Parse/I/O problems: Parse error: ... or I/O error: ...
Bad/partial rows are skipped and counted in skipped.
UML & Report
UML:
Report (PDF): docs/report_odev.pdf
Notes
Tested on Windows 10/11, Java 17 (Temurin).
Easily extensible: add new readers by implementing AddressReader (e.g., JSON/XLSX) without touching UI or stats logic.
