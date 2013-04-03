package net.sf.gilead.core.beanlib.mapper;

import junit.framework.TestCase;
import net.sf.gilead.core.beanlib.mapper.MultiDirectoryClassMapper;
import net.sf.gilead.core.beanlib.mapper.domain1.DomainClass1;
import net.sf.gilead.core.beanlib.mapper.domain1.dto.DomainClass1DTO;
import net.sf.gilead.core.beanlib.mapper.domain2.DomainClass2;
import net.sf.gilead.core.beanlib.mapper.dto2.DomainClass2DTO;

/**
 * Test case for Multi directory class mapper
 * @author Olaf Kock, Florian Siebert
 */
public class MultiDirectoryClassMapperTest extends TestCase {
	//----
	// Attributes
	//----
	/**
	 * The mapper to test
	 */
	private MultiDirectoryClassMapper mapper;
		
	//-------------------------------------------------------------------------
	//
	// Test init
	//
	//-------------------------------------------------------------------------
	/**
	 * Test initialisation
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mapper = new MultiDirectoryClassMapper();
		mapper.addMapping(DomainClass1.class.getPackage(), DomainClass1DTO.class.getPackage());
		mapper.addMapping(DomainClass2.class.getPackage(), DomainClass2DTO.class.getPackage());
		mapper.setTargetSuffix("DTO");
	}
	
	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test Source -> Clone mapping
	 */
	public void testSourceToCloneMapping() throws Exception {
		assertEquals(DomainClass1DTO.class, mapper.getTargetClass(DomainClass1.class));
		assertEquals(DomainClass2DTO.class, mapper.getTargetClass(DomainClass2.class));
	}
	
	/**
	 * Test Clone -> Source caching strategy
	 */
	public void testTargetClassCaching() {
		assertNull(mapper.getCachedAssociation(DomainClass1.class));
		assertNull(mapper.getCachedAssociation(DomainClass1DTO.class));
		
		assertEquals(DomainClass1DTO.class, mapper.getTargetClass(DomainClass1.class));
		
		assertEquals(DomainClass1DTO.class, mapper.getCachedAssociation(DomainClass1.class));
		assertEquals(DomainClass1.class, mapper.getCachedAssociation(DomainClass1DTO.class));
	}
	
	/**
	 * Test Source -> Clone caching strategy
	 */
	public void testSourceClassCaching() {
		assertNull(mapper.getCachedAssociation(DomainClass1.class));
		assertNull(mapper.getCachedAssociation(DomainClass1DTO.class));
		
		assertEquals(DomainClass1.class, mapper.getSourceClass(DomainClass1DTO.class));

		assertEquals(DomainClass1DTO.class, mapper.getCachedAssociation(DomainClass1.class));
		assertEquals(DomainClass1.class, mapper.getCachedAssociation(DomainClass1DTO.class));
	}

	/**
	 * Test Clone -> Source mapping
	 */
	public void testCloneToSourceMapping() throws Exception {
		assertEquals(DomainClass1.class, mapper.getSourceClass(DomainClass1DTO.class));
		assertEquals(DomainClass2.class, mapper.getSourceClass(DomainClass2DTO.class));
	}
}