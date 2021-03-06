/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.model;

import java.io.Serializable;

/**
 * Something made or exchanged by a Corporation(s).
 * @author Nicholas Maltbie
 */
public class Product implements Serializable
{
    /**Corporation that currently owns rights to the product.*/
    private Corporation supplier; 
    
    /**Name of the product*/
    private String productName;
    
    /**
     * Creates an instance of a product that owner has the rights to. Rights to
     * the product means that they are the corporation associated with the 
     * product.
     * @param owner Corporation that has rights to the product
     * @param productName Name of the product
     */
    public Product(Corporation owner, String productName)
    {
        this.supplier = owner;
        this.productName = productName;
    }
    
    /**
     * Gets the corporation that currently has rights to the product. If the 
     * rights change and a new Corporation controls the product, this method
     * can be called again to get the new owner.
     * @return Returns the Corporation that controls the product.
     */
    public Corporation getOwner()
    {
        return supplier;
    }
    
    /**
     * Sets the corporation that has rights to the product.
     * @param newOwner New corporation that will control the products.
     * @return Returns a false, subclases may have different uses for the
     * returned boolean.
     */
    public boolean exchangeOwner(Corporation newOwner)
    {
        this.supplier = newOwner;
        return false;
    }
    
    /**
     * Gets the product's name
     * @return Returns the product name field.
     */
    public String getProductName()
    {
        return productName;
    }
    
    @Override
    public String toString()
    {
        return productName;
    }
    
    /**
     * Checks if the product is controlled by the same corporation as
     * the the another Product other.
     * @param other Other product to check.
     * @return Returns if they have the same owner.
     */
    public boolean sameOwner(Product other)
    {
        return other.getOwner().equals(supplier);
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Product)
        {
            Product p = (Product) other;
            return (p.getOwner() == null && getOwner() == null) || 
                    (p.getOwner() != null && getOwner() != null && p.getOwner().equals(getOwner())) &&
                    getProductName().equals(p.getProductName());
        }
        return false;
    }
}
