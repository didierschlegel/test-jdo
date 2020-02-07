package mydomain.model;

import java.util.Set;

/**
 * Definition of an Inventory of products.
 * 
 * @author didier
 */
public interface IInventory {

	/**
	 * @return the products in this inventory
	 */
	Set<Product> getProducts();
	
}
