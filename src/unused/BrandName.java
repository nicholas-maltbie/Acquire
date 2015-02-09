/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package unused;

import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Corporation;
import java.util.Objects;

/**
 * Brand names are representative of a corporation's product and should be
 * used to tag game pieces associated with a corporation such as stocks.
 * A corporation could have different brand names for different products.
 * @author Nicholas Maltbie
 */
public class BrandName
{
    /**
     * Name of the brand; this is used to identify and display a product.
     */
    private String brandName;
    
    
    /**
     * Basic constructor, Brand name will take the corporateName and value then
     * assign them to immutable fields.
     * @param brandName - name of the brand.
     * @param corporation - corporation that owns the brand name.
     */
    public BrandName(String brandName, Corporation corporation)
    {
        this.brandName = brandName;
        this.corporation = corporation;
    }
    
    /**
     * Gets the corporation associated with this product.
     * @return returns the corporation field.
     */
    public Corporation getOwner()
    {
        return corporation;
    }
    
    /**
     * Returns the name of this brand.
     * @return returns the brandName field.
     */
    public String getName()
    {
        return brandName;
    }
    
    @Override
    public boolean equals(Object other)
    {
        try
        {
            BrandName brand = (BrandName) other;
            return brand.getOwner().equals(getOwner()) &&
                    brand.getName().equals(getName());
        }
        catch(ClassCastException e)
        {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.brandName);
        hash = 97 * hash + Objects.hashCode(this.corporation);
        return hash;
    }
    
    @Override
    public String toString()
    {
        return brandName + " owned by: " + corporation.toString();
    }
    
}
