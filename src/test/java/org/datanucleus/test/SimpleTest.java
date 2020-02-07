package org.datanucleus.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.util.NucleusLogger;
import org.junit.Test;

import mydomain.model.Inventory;
import mydomain.model.Product;

public class SimpleTest {
	@Test
	public void testSimple() {
		NucleusLogger.GENERAL.info(">> test START");
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Inventory inv = new Inventory("My Inventory");
			Product product = new Product("Sony Discman", "A standard discman from Sony", 200.00, inv);
			Product book = new Product("Lord of the Rings by Tolkien", "The classic story", 49.99, inv);
			inv.getProducts().add(product);
			inv.getProducts().add(book);
			pm.makePersistent(inv);
			tx.commit();
			Object inventoryId = pm.getObjectId(inv);
			assertNotNull(inventoryId);
			pm.close();
			
			pm = pmf.getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			Query<Inventory> qi = pm.newQuery(Inventory.class);
			assertEquals(1, qi.executeList().size());
			Query<Product> qp = pm.newQuery(Product.class);
			assertEquals(2, qp.executeList().size());
			tx.commit();
			pm.close();
			
			pm = pmf.getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			pm.deletePersistent(pm.getObjectById(inventoryId));
			tx.commit();
			pm.close();
			
			pm = pmf.getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			qi = pm.newQuery(Inventory.class);
			assertEquals(0, qi.executeList().size());
			qp = pm.newQuery(Product.class);
			assertEquals(0, qp.executeList().size());
			tx.commit();
		} catch (Throwable thr) {
			NucleusLogger.GENERAL.error(">> Exception in test", thr);
			fail("Failed test : " + thr.getMessage());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		pmf.close();
		NucleusLogger.GENERAL.info(">> test END");
	}
}
