package org.softlang.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.softlang.company.Company;
import org.softlang.company.Department;
import org.softlang.company.Employee;

/**
 * We do a round-trip test for de-/serialization.
 * That is, first, we create an object in memory.
 * Then, we write (say, serialize) the object.
 * Then, we read (say, de-serialize) the object.
 * Finally, we compare original and read object for structural equality.
 */
public class Serialization {

	public static Company createCompany() {
		
		// Create company
		Company company = new Company();
		company.setName("meganalysis");
		
		// Create all employees
		Employee craig = new Employee();
		craig.setName("Craig");
		craig.setAddress("Redmond");
		craig.setSalary(123456);
		craig.setCompany(company.getName());
		Employee erik = new Employee();
		erik.setName("Erik");
		erik.setAddress("Utrecht");
		erik.setSalary(12345);
		erik.setCompany(company.getName());
		Employee ralf = new Employee();
		ralf.setName("Ralf");
		ralf.setAddress("Koblenz");
		ralf.setSalary(1234);		
		ralf.setCompany(company.getName());
		Employee ray = new Employee();
		ray.setName("Ray");
		ray.setAddress("Redmond");
		ray.setSalary(234567);
		ray.setCompany(company.getName());
		Employee klaus = new Employee();
		klaus.setName("Klaus");
		klaus.setAddress("Boston");
		klaus.setSalary(23456);
		klaus.setCompany(company.getName());
		Employee karl = new Employee();
		karl.setName("Karl");
		karl.setAddress("Riga");
		karl.setSalary(2345);
		karl.setCompany(company.getName());
		Employee joe = new Employee();
		joe.setName("Joe");
		joe.setAddress("Wifi City");
		joe.setSalary(2344);	
		joe.setCompany(company.getName());

		// Create research department
		Department research = new Department();
		research.setManager(craig);
		research.setName("Research");
		research.getEmployees().add(erik);
		research.getEmployees().add(ralf);
		company.getDepts().add(research);

		// Create development department
		Department development = new Department();
		development.setManager(ray);
		development.setName("Development");
		company.getDepts().add(development);

		// Create sub-department dev1
		Department dev1 = new Department();
		development.getSubdepts().add(dev1);
		dev1.setName("Dev1");
		dev1.setManager(klaus);

		// Create sub-department dev11
		Department dev11 = new Department();
		dev1.getSubdepts().add(dev11);
		dev11.setName("Dev1.1");
		dev11.setManager(karl);
		dev11.getEmployees().add(joe);

		return company;
	}


	@Test
	public void testLoadAndCreate() {
		Company sampleCompany = createCompany(); 
		sampleCompany.writeObject("sampleCompany");
		Company loadedCompany = Company.readObject("sampleCompany");
		assertTrue(structurallyEqual(sampleCompany, loadedCompany));
	}
	
	
	
	public static boolean structurallyEqual(Object o1, Object o2) {

		try {
			if (o1.getClass().getName().equals(o2.getClass().getName())) {
				for (Class<?> obj = o1.getClass(); !obj.equals(Object.class); obj = obj
						.getSuperclass()) {

					Field[] f1 = obj.getDeclaredFields();
					Field[] f2 = obj.getDeclaredFields();

					for (int i = 0; i < f1.length; i++) {

						f1[i].setAccessible(true);
						f2[i].setAccessible(true);

						// check if fields are primitive types and compare
						if ((f1[i].getType().isPrimitive() && f2[i].getType()
								.isPrimitive())) {
							if (!(f1[i].getName().equals(f2[i].getName())))
								return false;
							else {
								if (!(f1[i].get(o1).equals(f2[i].get(o2)))) {
									return false;
								}
							}
							// otherwise, they must be objects
						} else {
							// to be equal, both can not be null
							if (f1[i].get(o1) != null && f2[i].get(o2) != null) {
								// check, if they are of the same class
								if (f1[i]
										.get(o1)
										.getClass()
										.getName()
										.equals(f2[i].get(o2).getClass()
												.getName())) {
									// check if the class is Double,Integer or
									// String
									if (check(f1[i].get(o1).getClass()
											.getName())) {
										// compare values
										if (!(f1[i].get(o1).equals(f2[i]
												.get(o2)))) {
											return false;
										}
									} else {
										// special case, if the object is an
										// linked
										// list
										if (f1[i].get(o1).getClass().getName()
												.equals("java.util.LinkedList")) {
											if (!(handleLinkedList(f1[i],
													o1, f2[i], o2))) {
												return false;
											}
										} else {
											List<Class> interfaces = new ArrayList();
											interfaces.add(f1[i].get(o1).getClass());
											interfaces = getSuperInterfaces(interfaces);
											if(interfaces.contains(Comparable.class)){
												//make use of WritableComparable's compare-method
												return ((Comparable)f1[i].get(o1)).compareTo((Comparable)f2[i].get(o2)) == 0;
											}else{
												// otherwise, compare the objects
												structurallyEqual(f1[i].get(o1),
													f2[i].get(o2));
											}	
										}
									}
								} else {
									return false;
								}
							} else {
								// if one of them is null, the objects can not
								// be
								// equal
								if (f1[i].get(o1) == null
										&& f2[i].get(o2) != null) {
									return false;
								}
								if (f1[i].get(o1) != null
										&& f2[i].get(o2) == null) {
									return false;
								}
							}

						}
					}
				}
			} else {
				return false;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean check(String s) {
		return (s.equals("java.lang.String") || s.equals("java.lang.Integer")
				|| s.equals("java.lang.Double") || s.equals("java.lang.Float"));

	}

	private static boolean handleLinkedList(Field f1, Object o1, Field f2, Object o2) {
		try {
			LinkedList<?> l1 = (LinkedList<?>) f1.get(o1);
			LinkedList<?> l2 = (LinkedList<?>) f2.get(o2);
			int length = l1.size();
			for (int i = 0; i < length; i++) {
				if (!(structurallyEqual(l1.get(i), l2.get(i)))) {
					return false;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return true;
	}	
	
	 public static List<Class> getSuperInterfaces(List<Class> childInterfaces) {

	        List<Class> allInterfaces = new ArrayList<Class>();

	        for (int i = 0; i < childInterfaces.size(); i++) {
	            allInterfaces.add(childInterfaces.get(i));
	            allInterfaces.addAll(
	                    getSuperInterfaces(Arrays.asList(childInterfaces.get(i).getInterfaces())));
	        }

	        return allInterfaces;
	  }
}