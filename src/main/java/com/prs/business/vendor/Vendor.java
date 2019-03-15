package com.prs.business.vendor;

import java.util.List;

import javax.persistence.*;

import com.prs.business.product.Product;

@SuppressWarnings("unused")
@Entity
public class Vendor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String code;
	private String name;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String phoneNumber;
	private String email;
	@Column(name = "IsPreApproved")
	private boolean preApproved;
//	@OneToMany(fetch=FetchType.EAGER, mappedBy="vendor")
//	private List<Product> products;
	
	public Vendor() {
	}
	
	public Vendor(int id, String code, String name, String address, String city, String state, String zip,
			String phoneNumber, String email, boolean preApproved) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.preApproved = preApproved;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isPreApproved() {
		return preApproved;
	}

	public void setPreApproved(boolean preApproved) {
		this.preApproved = preApproved;
	}

//	public List<Product> getProducts() {
//		return products;
//	}
//
//	public void setProducts(List<Product> products) {
//		this.products = products;
//	}

	@Override
	public String toString() {
		return "\n[" + id + "] " + name + "\n"
			 + "Code: " + code + " | Approved: " + preApproved + "\n"
			 + address + "\n"
			 + city + ", " + state + " " + zip + "\n"
			 + phoneNumber + " | " + email;
	}
	
}
