<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Edit User</title>
</head>
<body>
 
<h1>Edit User Data</h1>
 
<c:url var="saveUrl" value="/main/users/edit?id=${userAttribute.id}" />
<fieldset>
<legend>Sign up</legend>
<form:form modelAttribute="userAttribute" method="POST" commandName="userAttribute">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:errors path="username" />
		<form:label path="username" for="username">Username:</form:label>
		<form:input path="username" value="${userAttribute.username}"/>
	</p>
	<p>
		<form:errors path="name" />
		<form:label path="name" for="name">Full name:</form:label>
		<form:input path="name" value="${userAttribute.name}"/>
	</p>
	<p>
		<form:label path="user_type" for="user_type">Role:</form:label>
		<form:select path="user_type">
        <form:options items="${types}" />
		</form:select>
	</p>
	<p>
		<input name="send" type="submit" value="Save" />
	</p>
</form:form>
</fieldset>
 
</body>
</html>