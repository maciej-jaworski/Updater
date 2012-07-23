<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Edit Password</title>
</head>
<body>
 
<h1>Change Your Password</h1>
 
<c:url var="saveUrl" value="/users/changepw" />
<fieldset>
<legend>Change Password</legend>
<form:form modelAttribute="pe" method="POST" commandName="pe">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:input type="hidden" path="userId" value="${pe.userId}"/>
	</p>
	<p>
		<form:label path="currentPw" for="currentPw">Current Password:</form:label>
		<form:input path="currentPw" type="password"/>
	</p>
	<p>
		<form:label path="newPw" for="newPw">New Password:</form:label>
		<form:input path="newPw" type="password"/>
	</p>
	<p>
		<form:label path="confirmPw" for="confirmPw">Confirm New Password:</form:label>
		<form:input path="confirmPw" type="password"/>
	</p>	
	<p>
		<input name="send" type="submit" value="Save" />
	</p>
</form:form>
</fieldset>
 
</body>
</html>