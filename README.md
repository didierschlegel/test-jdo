test-jdo
========

Template project for any user testcase using JDO.
To create a DataNucleus test simply fork this project, and add/edit as 
necessary to add your model and persistence commands. The files that you'll likely need to edit are

* <a href="https://github.com/datanucleus/test-jdo/tree/master/src/main/java/mydomain/model">src/main/java/mydomain/model/</a>   **[Put your model classes here]**
* <a href="https://github.com/datanucleus/test-jdo/blob/master/src/main/resources/META-INF/persistence.xml">src/main/resources/META-INF/persistence.xml</a>   **[Put your datastore details in here]**
* <a href="https://github.com/datanucleus/test-jdo/blob/master/src/test/java/org/datanucleus/test/SimpleTest.java">src/test/java/org/datanucleus/test/SimpleTest.java</a>   **[Edit this if a single-thread test is required]**
* <a href="https://github.com/datanucleus/test-jdo/blob/master/src/test/java/org/datanucleus/test/MultithreadTest.java">src/test/java/org/datanucleus/test/MultithreadTest.java</a>   **[Edit this if a multi-thread test is required]**

To run this, simply type "mvn clean compile test"

This fork contains the following test cases:
===========================================
1. Referential integrity constraint violation with datanucleus and bidirectional 1-N in optimistic locking when using interface type
 
   **see test class InterfaceRelationTest.java**
   
   There is a 1-N bidirectional relation whith dependent elements and where the mapped-by field is an interface-type.
   
   ```java
   public interface IInventory {
     /**
      * @return the products in this inventory
      */
     Set<Product> getProducts(); 
   }
   
   @PersistenceCapable
   public class Inventory implements IInventory
   {
      @PrimaryKey
      protected String name=null;

      @Persistent(mappedBy="inventory")
      @Join
      @Element(dependent="true")
      protected Set<Product> products = new HashSet<Product>();
   
      ...
   }

   @PersistenceCapable
   public class Product
   {
      @PrimaryKey
      @Persistent(valueStrategy=IdGeneratorStrategy.NATIVE)
      protected long id;

      @Persistent(types=Inventory.class)
      protected IInventory inventory=null;

      ...
   }
   ```

   The test case runs successfully with "javax.jdo.option.Optimistic" set to "false", but when changing it to "true" the following exception is thrown:
   ```
   18:31:45,057 (main) WARN  [DataNucleus.Datastore.Persist] - Delete of object "mydomain.model.Product@565f390" using statement "DELETE FROM PRODUCT WHERE ID=?" failed : Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
   18:31:45,057 (main) DEBUG [DataNucleus.Persistence] - ExecutionContext.internalFlush() END
   18:31:45,058 (main) DEBUG [DataNucleus.Transaction] - Delete of object "mydomain.model.Product@565f390" using statement "DELETE FROM PRODUCT WHERE ID=?" failed : Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
   org.datanucleus.exceptions.NucleusDataStoreException: Delete of object "mydomain.model.Product@565f390" using statement "DELETE FROM PRODUCT WHERE ID=?" failed : Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
    at org.datanucleus.store.rdbms.request.DeleteRequest.execute(DeleteRequest.java:378)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObjectFromTable(RDBMSPersistenceHandler.java:494)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObject(RDBMSPersistenceHandler.java:466)
    at org.datanucleus.state.StateManagerImpl.internalDeletePersistent(StateManagerImpl.java:1207)
    at org.datanucleus.state.StateManagerImpl.flush(StateManagerImpl.java:5789)
    at org.datanucleus.flush.FlushOrdered.execute(FlushOrdered.java:132)
    at org.datanucleus.ExecutionContextImpl.flushInternal(ExecutionContextImpl.java:4063)
    at org.datanucleus.ExecutionContextImpl.flush(ExecutionContextImpl.java:4009)
    at org.datanucleus.ExecutionContextImpl.preCommit(ExecutionContextImpl.java:4184)
    at org.datanucleus.ExecutionContextImpl.transactionPreCommit(ExecutionContextImpl.java:729)
    at org.datanucleus.TransactionImpl.internalPreCommit(TransactionImpl.java:397)
    at org.datanucleus.TransactionImpl.commit(TransactionImpl.java:287)
    at org.datanucleus.api.jdo.JDOTransaction.commit(JDOTransaction.java:107)
    at org.datanucleus.test.SimpleTest.testSimple(SimpleTest.java:53)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:69)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:48)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:292)
    at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
    at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
    at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
    at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
    at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:113)
    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
   Caused by: org.h2.jdbc.JdbcBatchUpdateException: Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
    at org.h2.jdbc.JdbcPreparedStatement.executeBatch(JdbcPreparedStatement.java:1121)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.ParamLoggingPreparedStatement.executeBatch(ParamLoggingPreparedStatement.java:366)
    at org.datanucleus.store.rdbms.SQLController.processConnectionStatement(SQLController.java:667)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:233)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:176)
    at org.datanucleus.store.rdbms.request.DeleteRequest.execute(DeleteRequest.java:289)
    ... 42 more
   Nested Throwables StackTrace:
   org.h2.jdbc.JdbcBatchUpdateException: Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES    PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
    at org.h2.jdbc.JdbcPreparedStatement.executeBatch(JdbcPreparedStatement.java:1121)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.ParamLoggingPreparedStatement.executeBatch(ParamLoggingPreparedStatement.java:366)
    at org.datanucleus.store.rdbms.SQLController.processConnectionStatement(SQLController.java:667)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:233)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:176)
    at org.datanucleus.store.rdbms.request.DeleteRequest.execute(DeleteRequest.java:289)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObjectFromTable(RDBMSPersistenceHandler.java:494)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObject(RDBMSPersistenceHandler.java:466)
    at org.datanucleus.state.StateManagerImpl.internalDeletePersistent(StateManagerImpl.java:1207)
    at org.datanucleus.state.StateManagerImpl.flush(StateManagerImpl.java:5789)
    at org.datanucleus.flush.FlushOrdered.execute(FlushOrdered.java:132)
    at org.datanucleus.ExecutionContextImpl.flushInternal(ExecutionContextImpl.java:4063)
    at org.datanucleus.ExecutionContextImpl.flush(ExecutionContextImpl.java:4009)
    at org.datanucleus.ExecutionContextImpl.preCommit(ExecutionContextImpl.java:4184)
    at org.datanucleus.ExecutionContextImpl.transactionPreCommit(ExecutionContextImpl.java:729)
    at org.datanucleus.TransactionImpl.internalPreCommit(TransactionImpl.java:397)
    at org.datanucleus.TransactionImpl.commit(TransactionImpl.java:287)
    at org.datanucleus.api.jdo.JDOTransaction.commit(JDOTransaction.java:107)
    at org.datanucleus.test.SimpleTest.testSimple(SimpleTest.java:53)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:69)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:48)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:292)
    at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
    at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
    at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
    at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
    at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:113)
    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
   org.h2.jdbc.JdbcSQLException: Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
    at org.h2.message.DbException.getJdbcSQLException(DbException.java:329)
    at org.h2.message.DbException.get(DbException.java:169)
    at org.h2.message.DbException.get(DbException.java:146)
    at org.h2.constraint.ConstraintReferential.checkRow(ConstraintReferential.java:414)
    at org.h2.constraint.ConstraintReferential.checkRowRefTable(ConstraintReferential.java:431)
    at org.h2.constraint.ConstraintReferential.checkRow(ConstraintReferential.java:307)
    at org.h2.table.Table.fireConstraints(Table.java:871)
    at org.h2.table.Table.fireAfterRow(Table.java:888)
    at org.h2.command.dml.Delete.update(Delete.java:99)
    at org.h2.command.CommandContainer.update(CommandContainer.java:75)
    at org.h2.command.Command.executeUpdate(Command.java:230)
    at org.h2.jdbc.JdbcPreparedStatement.executeUpdateInternal(JdbcPreparedStatement.java:156)
    at org.h2.jdbc.JdbcPreparedStatement.executeBatch(JdbcPreparedStatement.java:1106)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.ParamLoggingPreparedStatement.executeBatch(ParamLoggingPreparedStatement.java:366)
    at org.datanucleus.store.rdbms.SQLController.processConnectionStatement(SQLController.java:667)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:233)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:176)
    at org.datanucleus.store.rdbms.request.DeleteRequest.execute(DeleteRequest.java:289)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObjectFromTable(RDBMSPersistenceHandler.java:494)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObject(RDBMSPersistenceHandler.java:466)
    at org.datanucleus.state.StateManagerImpl.internalDeletePersistent(StateManagerImpl.java:1207)
    at org.datanucleus.state.StateManagerImpl.flush(StateManagerImpl.java:5789)
    at org.datanucleus.flush.FlushOrdered.execute(FlushOrdered.java:132)
    at org.datanucleus.ExecutionContextImpl.flushInternal(ExecutionContextImpl.java:4063)
    at org.datanucleus.ExecutionContextImpl.flush(ExecutionContextImpl.java:4009)
    at org.datanucleus.ExecutionContextImpl.preCommit(ExecutionContextImpl.java:4184)
    at org.datanucleus.ExecutionContextImpl.transactionPreCommit(ExecutionContextImpl.java:729)
    at org.datanucleus.TransactionImpl.internalPreCommit(TransactionImpl.java:397)
    at org.datanucleus.TransactionImpl.commit(TransactionImpl.java:287)
    at org.datanucleus.api.jdo.JDOTransaction.commit(JDOTransaction.java:107)
    at org.datanucleus.test.SimpleTest.testSimple(SimpleTest.java:53)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:69)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:48)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:292)
    at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
    at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
    at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
    at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
    at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:113)
    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
   org.h2.jdbc.JdbcSQLException: Referential integrity constraint violation: "PRODUCT_FK1: PUBLIC.PRODUCT FOREIGN KEY(INVENTORY_INVENTORY_NAME_EID) REFERENCES PUBLIC.INVENTORY(NAME) ('My Inventory')"; SQL statement:
   DELETE FROM INVENTORY WHERE "NAME"=? [23503-168]
    at org.h2.message.DbException.getJdbcSQLException(DbException.java:329)
    at org.h2.message.DbException.get(DbException.java:169)
    at org.h2.message.DbException.get(DbException.java:146)
    at org.h2.constraint.ConstraintReferential.checkRow(ConstraintReferential.java:414)
    at org.h2.constraint.ConstraintReferential.checkRowRefTable(ConstraintReferential.java:431)
    at org.h2.constraint.ConstraintReferential.checkRow(ConstraintReferential.java:307)
    at org.h2.table.Table.fireConstraints(Table.java:871)
    at org.h2.table.Table.fireAfterRow(Table.java:888)
    at org.h2.command.dml.Delete.update(Delete.java:99)
    at org.h2.command.CommandContainer.update(CommandContainer.java:75)
    at org.h2.command.Command.executeUpdate(Command.java:230)
    at org.h2.jdbc.JdbcPreparedStatement.executeUpdateInternal(JdbcPreparedStatement.java:156)
    at org.h2.jdbc.JdbcPreparedStatement.executeBatch(JdbcPreparedStatement.java:1106)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.datasource.dbcp2.DelegatingStatement.executeBatch(DelegatingStatement.java:345)
    at org.datanucleus.store.rdbms.ParamLoggingPreparedStatement.executeBatch(ParamLoggingPreparedStatement.java:366)
    at org.datanucleus.store.rdbms.SQLController.processConnectionStatement(SQLController.java:667)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:233)
    at org.datanucleus.store.rdbms.SQLController.getStatementForUpdate(SQLController.java:176)
    at org.datanucleus.store.rdbms.request.DeleteRequest.execute(DeleteRequest.java:289)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObjectFromTable(RDBMSPersistenceHandler.java:494)
    at org.datanucleus.store.rdbms.RDBMSPersistenceHandler.deleteObject(RDBMSPersistenceHandler.java:466)
    at org.datanucleus.state.StateManagerImpl.internalDeletePersistent(StateManagerImpl.java:1207)
    at org.datanucleus.state.StateManagerImpl.flush(StateManagerImpl.java:5789)
    at org.datanucleus.flush.FlushOrdered.execute(FlushOrdered.java:132)
    at org.datanucleus.ExecutionContextImpl.flushInternal(ExecutionContextImpl.java:4063)
    at org.datanucleus.ExecutionContextImpl.flush(ExecutionContextImpl.java:4009)
    at org.datanucleus.ExecutionContextImpl.preCommit(ExecutionContextImpl.java:4184)
    at org.datanucleus.ExecutionContextImpl.transactionPreCommit(ExecutionContextImpl.java:729)
    at org.datanucleus.TransactionImpl.internalPreCommit(TransactionImpl.java:397)
    at org.datanucleus.TransactionImpl.commit(TransactionImpl.java:287)
    at org.datanucleus.api.jdo.JDOTransaction.commit(JDOTransaction.java:107)
    at org.datanucleus.test.SimpleTest.testSimple(SimpleTest.java:53)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:69)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:48)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:292)
    at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
    at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
    at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
    at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
    at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:113)
    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
   ```

   Changing the mapped-by field to Inventory-Type (no more interface) the main runs also successfully in Optimistic mode.
   ```java
   @PersistenceCapable
   public class Product
   {
      @PrimaryKey
      @Persistent(valueStrategy=IdGeneratorStrategy.NATIVE)
      protected long id;

      protected Inventory inventory=null;

      ...
   }
   ```

