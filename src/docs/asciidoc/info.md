# Fuzzer Design

## Levels

Generating code should be in levels of detail, along with desired constraints as such:

Kind = What kind of thing it is, for example:
       - a Program may be a "Mathematics Library"
       - a Variable may be a "Map"
Scope = How long the thing lasts;
       - a Variable may only exist within a Function, or within a Program, or even remain on Disk
       - a Program may be created dynamically by anonther Program
Accessibility = How accessible the thing is;
       - a Function may be public, or private
       - a Program may be public, or private
       - a Variable may be directly writeable

| Level      | Defines    | Kind | Scope | Accessibility |
|------------|------------|------|-------|---------------|
| Atlas      | Programs   | x    | x     | x             |
| Programs   | Functions  | x    |       | x             |
| Programs   | Variables  | x    | x     | x             |
| Functions  | Statements | x    |       |               |
| Functions  | Variables  | x    | x     |               |
| Statements | Statements | x    |       |               |

Defining these allows for constraints to be added, where a certain Definition must be of a given Kind, Scope or Accessibility, for example;
- Require that only one Program must exist, which is of kind "WALLET" owning at least 5 amount of Currency
- Require that at least one "Publicly Accessible" Function must exist, containing a Statement of type "LOOP\_XL" followed by a Statement of type "VULNERABLE\_CALL"

These are essentially templates which can be expanded with additional definitions.

# Code Design

Try out Clojure or some other lisp? If just to get the concepts

The following would be nice;
- More pure functions
- REPL development in combination with the above two
