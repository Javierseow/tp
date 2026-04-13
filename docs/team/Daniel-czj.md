# Project Portfolio Page - Daniel-czj

## Overview
FitLogger is a command-line workout tracking application for users who prefer fast keyboard-driven logging and editing of workout records.

My main focus was improving workout editing and deletion workflows, adding date-based search, improving exit behavior, and hardening parser/profile formatting logic to reduce user-facing errors.

## Summary of Contributions

### Code Contributed
- Code Dashboard link:(https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=daniel-czj&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enhancements Implemented
- Implemented and refined `EditCommand` behavior and test coverage.
- Implemented and refined `DeleteCommand` (index-based deletion flow) and tests.
- Added `search-date` feature (`SearchDateCommand`) and parser integration.
- Improved `ExitCommand` save and feedback behavior.
- Added save-failure handling paths for command execution with corresponding tests.
- Added validation to reject invalid numeric values (`NaN`, `Infinity`) in workout creation/edit flows.
- Hardened numeric parsing to reject scientific notation and overlarge numeric inputs in add/edit flows.
- Added a 1,000,000 upper bound for workout indexes, shortcut IDs, sets, and reps to prevent integer-overflow edge cases and unrealistic inputs.
- Tightened parser arity checks for `delete`, `search-date`, and `exit` so extra arguments are rejected instead of being silently ignored.
- Redirected Java logging to `logs/fitlogger.log` so diagnostic logs remain available without cluttering the CLI output.
- Added constructor-level safeguards in workout/domain objects to enforce valid object state at creation time.

### Bug Fixes and Technical Improvements
- Parser numeric parsing fix:
  - Updated parser height/weight parse logic to use:
    - `double newValue = Double.parseDouble(value);`
- Parser validation hardening:
  - Added plain-decimal validation for workout weight, distance, and duration fields.
  - Added bounded integer parsing for indexes, shortcut IDs, sets, and reps.
  - Added regression tests for scientific notation, integer overflow, extra command arguments, and overlarge values.
- Profile display formatting improvement:
  - Updated height display formatting to:
    - `String.format("%.2fm", profile.getHeight())`

### Contributions to User Guide
- Wrote and refined sections for:
  - `add-lift`
  - `edit`
  - `delete`
  - `search-date`
  - `exit`
- Documented numeric input limits, plain-decimal requirements, and logging behavior.
- Added practical usage examples, invalid-input examples, and expected error outputs.


### Contributions to Developer Guide
- Wrote and improved design and implementation documentation for:
  - `EditCommand`
  - `DeleteCommand`
  - `search-date`
  - command save-failure handling
- Added design notes for parser validation hardening and file-based logging.
- Updated command architecture descriptions to match current `execute(storage, workouts, ui, profile)` signature.

### Contributions to Team-Based Tasks
- Contributed feature implementation and bug-fix integration across command, parser, and tests.
- Supported command-related quality improvements and consistency checks before submission.

### Review and Mentoring Contributions
- Reviewed pull requests:
  - PR #[65] (https://github.com/AY2526S2-CS2113-F09-1/tp/pull/65)
- Provided implementation feedback on parser and command behavior consistency, especially for double parsing and storage handling.
