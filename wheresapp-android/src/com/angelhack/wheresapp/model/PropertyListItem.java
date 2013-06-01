package com.angelhack.wheresapp.model;

public class PropertyListItem extends BaseObject {

	public String listing_id;
	public String property_id;
	public String property_name;
	public String price;
	public String image_url;
	public double latitude;
	public double longitude;

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object.getClass().getName().equals(this.getClass().getName()))) {
			return false;
		}
		PropertyListItem propertyListItem = (PropertyListItem) object;
		if (propertyListItem.listing_id != null
				&& propertyListItem.listing_id.equals(this.listing_id)) {

			return true;
		}
		return false;
	}

}