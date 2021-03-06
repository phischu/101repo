package org.softlang.basics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.softlang.util.FileInputStreamFactory;

public class Company implements Serializable {

	private static final long serialVersionUID = -200889592677165250L;
	private Long id;
	private String name;
	private List<Department> depts;

	public Company() {
		depts = new LinkedList<Department>();
	}

	/**
	 * Read (say, deserialize) a company
	 */
	public static Company readObject(String filename) {

		Object o = null;

		try {
			FileInputStream fis = FileInputStreamFactory.createFileInputStream("sampleCompany.ser");
			ObjectInputStream in = new ObjectInputStream(fis);
			o = in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (Company) o;
	}

	/**
	 * Write (say, serialize) an object.
	 */
	public boolean writeObject(String filename) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Department> getDepts() {
		return depts;
	}
	

	public Double getTotal() {
		double total = 0;
		for (Department d : getDepts())
			total += d.getTotal();
		return total;
	}

	public void cut() {
		for (Department d : getDepts())
			d.cut();
	}

	public Department findDepartment(Long id) {
		for (Department d : depts) {
			Department result = d.findDepartment(id);
			if(result != null) {
				return result;
			}
		}
		return null;
	}

}
