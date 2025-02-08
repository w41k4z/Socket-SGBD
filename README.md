## Syntax Check Documentation

### Author
**Rakotondranaivo Alain Rico**

## Overview
This document provides a syntax guide for operations based on a **whitespace splitter**. The syntax is divided into three categories:

1. **Data Definition Syntax (DDS)** – `USE`, `CREATE`, `DROP`
2. **Data Manipulation Syntax (DMS)** – `ADD`, `UPDATE`, `DELETE`
3. **Data Query Syntax (DQS)** – `SHOW`, `GET`

---

## 1. Data Definition Syntax (DDS) – `USE`, `CREATE`, `DROP`

Operations for managing databases and relations.

### Examples:
```plaintext
USE DATABASE Test
```
Grants access to the `Test` database.

```plaintext
CREATE RELATION R1<<col1, col2, col3>>
```
Creates a relation (`R1`) with specified columns.

```plaintext
DROP DATABASE Test
```
Deletes the `Test` database.

### Notes:
- `RELATION` and `DATABASE` names **must not** conflict with reserved keywords or existing objects.

---

## 2. Data Manipulation Syntax (DMS) – `ADD`, `UPDATE`, `DELETE`

Operations for modifying data within a relation.

### Examples:
```plaintext
ADD INTO R1 VALUES<<val1, val2, val3>>
```
or
```plaintext
ADD INTO R1<<col1, col3>> VALUES<<val1, val3>>
```
Inserts values into specified columns of `R1`.

```plaintext
UPDATE R1 SET col1=val2, col2=val3 WHERE colX=valX_OPERATOR_colY=valY
```
Updates values based on the condition.

```plaintext
DELETE FROM R1 WHERE colX=valX_OPERATOR_colY=valY
```
Deletes rows matching the condition.

### Notes:
- The `WHERE` clause follows this convention:
```plaintext
WHERE colX=valX_OPERATOR_colY=valY
```
`OPERATOR` can be `AND` or `OR`.

---

## 3. Data Query Syntax (DQS) – `SHOW`, `GET`

Operations for retrieving information.

### Examples:
```plaintext
SHOW ALL DATABASE
```
Displays a list of all databases.

```plaintext
GET ALL FROM R1 WHERE colX=valX_OPERATOR_colY=valY
```
Returns all matching rows from `R1`.

```plaintext
GET col1, col2 FROM R1 WHERE colX=valX_OPERATOR_colY=valY
```
Returns only specified columns from `R1`.

### Notes:
- `WHERE` syntax must follow:
```plaintext
WHERE colX=valX_OPERATOR_colY=valY
```
`OPERATOR` can be `AND` or `OR`.

---

## Final Notes
- Always ensure correct syntax and naming conventions.
- Be mindful of reserved keywords when defining names.
