package net.sf.gilead.test.domain.proxy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * User Domain class for statefull server
 */
public class User implements Serializable, IUser
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 7239533396817246837L;
	
	// Fields
	private Integer id;
	private Integer version;
	
	private String login;
	private String firstName;
	private String lastName;
	private String password;
	
	private IAddress address;

	private Set<IMessage> messageList;
	private Set<IGroup> groupList;

	// Properties
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getId()
	 */
	public Integer getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getVersion()
	 */
	public Integer getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getLogin()
	 */
	public String getLogin() {
		return this.login;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setLogin(java.lang.String)
	 */
	public void setLogin(String surname) {
		this.login = surname;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getFirstName()
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setFirstName(java.lang.String)
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getLastName()
	 */
	public String getLastName() {
		return this.lastName;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setLastName(java.lang.String)
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#getPassword()
	 */
	public String getPassword() {
		return this.password;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.stateful.IUser#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the message List
	 */
	public Set<IMessage> getMessageList() {
		return messageList;
	}

	/**
	 * @param messageList the message List to set
	 */
	public void setMessageList(Set<IMessage> messageList) {
		this.messageList = messageList;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IUser#addMessage(net.sf.gilead.testApplication.domain.IMessage)
	 */
	public void addMessage(IMessage message)
	{
		// Bi-directional association
		((Message)message).setAuthor(this);
		
		// Create message list if needed
		if (messageList == null)
		{
			messageList = new HashSet<IMessage>();
		}
		messageList.add(message);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IUser#removeMessage(net.sf.gilead.testApplication.domain.IMessage)
	 */
	public void removeMessage(IMessage message)
	{
		messageList.remove(message);
	}
	/**
	 * @return the groupList
	 */
	public Set<IGroup> getGroupList() {
		return groupList;
	}

	/**
	 * @param groupList the groupList to set
	 */
	public void setGroupList(Set<IGroup> groupList) {
		this.groupList = groupList;
	}
	
	/**
	 * Add user to the argument group
	 */
	public void addToGroup(IGroup group)
	{
		if (groupList == null)
		{
			groupList = new HashSet<IGroup>();
		}
		
		if (groupList.contains(group) == false)
		{
			groupList.add(group);
			group.addMember(this);
		}
	}

	/**
	 * Remove user from group
	 */
	public void removeUserFromGroup(IGroup group)
	{
		if ((groupList != null) &&
			(groupList.contains(group)))
		{
			groupList.remove(group);
			group.removeMember(this);
		}
	}

	public IAddress getAddress()
	{
		return address;
	}

	public void setAddress(IAddress address)
	{
		this.address = address;
	}
}
