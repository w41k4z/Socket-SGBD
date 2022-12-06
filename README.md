Author : Rakotondranaivo Alain Rico (ETU002011)

Syntax check based on white space splitter

Three valid syntaxes :
     1) Data Definition Syntax or DDS (USE-CREATE-DROP)
        ex1: USE DATABASE Test (DATABASE only)
          => accessing the database to be able to do operations
        ex2: CREATE RELATION R1<<col1,col2,col3>>
          => creating a new Relation (database object)
        ex3: DROP DATABASE Test
          => dropping the database
        NB: RELATION/DATABASE name must no be named like a database object, query
          => WHERE colX=valX_OPERATOR_colY=valY (OPERATOR: AND,OR)
      2) Data Manipulation Syntax or DMS (ADD-UPDATE-DELETE)
        ex1: ADD INTO R1 VALUES<<val1,val2,val3>> || ADD INTO R1<<col1,col3>> VALUES<<val1,val3>>
          => insert a new line of data for the relation
        ex2: UPDATE R1 SET col1=val2,col2=val3... (+ WHERE condition)
          => update data depending on the condition
        ex3: DELETE FROM R1 (+ WHERE condition)
          => delete data depending on the condition
        NB: WHERE condition has a little convention
          => WHERE colX=valX_OPERATOR_colY=valY (OPERATOR: AND,OR)
      3) Data Query Syntax or DQS (SHOW-GET)
        ex1: SHOW ALL DATABASE
          => show all existing database
        ex2: GET ALL FROM R1 (+ WHERE condition) (Relation only)
          => return the specified data with the specified column
        NB: Column can be specified for the GET query
          => GET col1,col2 FROM R1 (+ WHERE condition)
