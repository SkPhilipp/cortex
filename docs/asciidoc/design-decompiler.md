== Decompile Rules

"any" indicates either `*` or `*-if`.

Rule:
- Pattern `@start ... jump-if @start`                      == `@start do-while { ... }`
- Pattern `do-while { ... jump-any @end ... } @end`        == `do-while { ... break-any ... } @end`
- Pattern `jump-if @forward ... @forward`                  == `if-not { ... } @forward`
- Pattern `if-not { ... jump @end } ... @end`              == `if-not { ... } else { ... } @end
- Pattern `@start ... jump @start`                         == `@start while (true) { ... }`
- Pattern `while (...) { ... jump-any @end ... } @end`    == `while (...) { ... break-any ... } @end`
- Pattern `@start while (...) { ... jump-any @start ...}`  == `@start while (...) { ... continue-any ... }`
- Pattern `while (true) { ... break-if ... }`              == `while (...) { ... }`
- Pattern `... jump-dynamic`                               == `... return`
- ?                                                        == `function { ... }`
