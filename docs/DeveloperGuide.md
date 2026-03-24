# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Enhancement 1: `EditCommand`

#### Purpose and user value

`EditCommand` lets users modify an existing workout entry without deleting and recreating it.
This reduces friction when correcting common input mistakes (for example, wrong distance, duration,
sets, reps, weight, name, or description) while preserving the original workout ordering.

Supported fields:
- For all workouts: `name`, `description`
- For run workouts: `distance`, `duration`
- For strength workouts: `weight`, `sets`, `reps`

Command format:

```
edit <index> <field>/<value>
```

Examples:
- `edit 1 distance/4.67`
- `edit 2 reps/10`
- `edit 3 name/Tempo Run`

#### Design overview

At the architecture level, this enhancement follows FitLogger's existing command pipeline:

1. `Parser.parse(...)` receives raw user input.
2. `Parser.parseEdit(...)` validates format and extracts the index, field, and value.
3. `Parser` returns an `EditCommand` as a `Command`.
4. The runtime invokes `Command.execute(storage, workouts, ui)` polymorphically.
5. `EditCommand` updates the selected `Workout` and reports the result through `Ui`.

Responsibilities remain clearly separated:
- Parser handles *syntax checking and tokenization*.
- Command handles *execution logic and field dispatch*.
- Workout classes enforce *domain invariants* through setter validation.

#### Component-level behavior

`EditCommand.execute(...)` performs the following steps:

1. Convert the user index from one-based to zero-based.
2. Validate bounds (`index >= 1 && index <= workouts.getSize()`).
3. Retrieve the target workout from `WorkoutList`.
4. Dispatch by field name using a switch statement.
5. Validate workout type compatibility for type-specific fields:
	- Reject `weight/sets/reps` for run workouts.
	- Reject `distance/duration` for strength workouts.
6. Parse numeric values for numeric fields.
7. Delegate final validation to domain setters.
8. Show a clear success or error message.

The command is intentionally defensive:
- Unknown fields are rejected (`Unknown editable field: ...`).
- Non-numeric numeric inputs are rejected.
- Invalid domain values are rejected via `FitLoggerException`.
- Name/description edits are validated against reserved storage delimiters (`|` and `/`) to prevent save-file corruption.

#### Data integrity and validation decisions

Key safeguards:

- **Delimiter safety**: edited names/descriptions are rejected when they contain reserved storage separators.
- **Finite numeric values**: `NaN` and `Infinity` are rejected for distance, duration, and weight.
- **Domain constraints**:
  - distance, duration > 0
  - weight >= 0
  - sets, reps > 0

These rules prevent invalid in-memory state and malformed persisted data.

#### Testing strategy

`EditCommandTest` covers both success and failure paths:

- Valid updates for run and strength workouts.
- Invalid index handling.
- Type mismatch handling (for example, lift-only fields on run workouts).
- Delimiter injection prevention for edited names.
- Rejection of non-finite numeric values (`NaN`, `Infinity`).

This verifies robust behavior under realistic user mistakes and malformed input.

#### Example usage scenario

Given below is an example scenario of how `EditCommand` is processed.

**Step 1.** The user enters an edit command, for example `edit 2 reps/10`.

**Step 2.** `Parser.parse(...)` routes the input to `Parser.parseEdit(...)`, which validates the command format and
extracts the index, field, and value.

**Step 3.** `Parser` returns an `EditCommand` object to the main execution loop.

**Step 4.** During `EditCommand.execute(storage, workouts, ui)`, the command validates index bounds, checks field
compatibility with workout type, and parses/validates the new value.

**Step 5.** The target workout is updated through setter methods, and a success message is printed. If any validation
fails, an error message is shown instead.

---

### Enhancement 2: `DeleteCommand`

#### Purpose and user value

`DeleteCommand` allows users to remove workouts by index or by name.
This supports both quick positional deletion and direct name-based deletion,
depending on whether users remember list order or workout name.

Supported formats:
- `delete <index>`
- `delete <workout_name>`

#### Design overview

The command is intentionally simple and cohesive:

1. Parser identifies the `delete` command and passes the raw argument string.
2. `DeleteCommand` stores only the user-supplied target text.
3. During execution, the command resolves the target as index-or-name.
4. If found, workout is removed from `WorkoutList`; otherwise, user sees not-found feedback.

This design keeps parsing and command behavior focused while preserving compatibility with the existing pipeline.

#### Component-level behavior

`DeleteCommand.execute(...)` performs:

1. Empty input check:
	- If blank, show usage guidance and return early.
2. Target resolution (`findWorkoutIndex(...)`):
	- First, attempt numeric parsing (`parseUserProvidedIndex`).
	- If numeric, convert one-based user index to zero-based list index.
	- If non-numeric, perform case-insensitive full-name match.
3. Deletion:
	- On match, delete the workout and show `Deleted workout: <name>`.
	- On miss, show `Workout not found: <input>`.

This approach avoids ambiguity and keeps deletion behavior predictable.

#### Edge cases handled

- Blank target text.
- Out-of-range numeric index (e.g., 0 or larger than current list size).
- Non-numeric text that does not match any workout name.
- Case-insensitive name matching for better usability.

#### Testing strategy

`DeleteCommandTest` verifies:

- Name-based deletion success.
- Index-based deletion success (one-based input behavior).
- Blank-input usage message.
- Not-found message for missing name.
- Not-found message for invalid numeric index.

This ensures both deletion flows (index and name) remain stable and regressions are caught early.

#### Example usage scenario

Given below is an example scenario of how `DeleteCommand` is processed.

**Step 1.** The user enters a delete command, for example `delete 3` or `delete Tempo Run`.

**Step 2.** `Parser.parse(...)` identifies the `delete` command and creates a `DeleteCommand` with the raw argument.

**Step 3.** In `DeleteCommand.execute(storage, workouts, ui)`, the command first checks for empty input.

**Step 4.** The command resolves the target by trying numeric index parsing first, then case-insensitive name matching
if parsing fails.

**Step 5.** If a target workout is found, it is removed from `WorkoutList` and the user sees a deletion confirmation.
If no target is found, the user sees a not-found message.

---

### Notes for team writeups

For each team member section, use this practical structure:

1. Problem statement and user value
2. Design decisions and alternatives considered
3. Class/method-level implementation details
4. Validation and error-handling strategy
5. Test coverage and current limitations

This format communicates implementation depth clearly and consistently.


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
