<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Register</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Create New User</h1>

<fieldset>
<legend>Sign up</legend>
<form:form modelAttribute="user" method="POST" commandName="user">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:label path="username" for="username">Username:</form:label>
		<form:input path="username" />
	</p>
	<p>
		<form:label path="password" for="password">Password:</form:label>
		<form:input path="password" type="password" />
	</p>
	<p>
		<form:label path="confirmPassword" for="confirmPassword">Confirm password:</form:label>
		<form:input path="confirmPassword" type="password" />
	</p>
	<p>
		<form:label path="name" for="name">Full name:</form:label>
		<form:input path="name" />
	</p>
	<p>
		<form:label path="user_type" for="user_type">Role:</form:label>
		<form:select path="user_type">
        <form:options items="${types}" />
		</form:select>
	</p>
	<p>
		<input name="send" type="submit" value="Sign up" />
	</p>
</form:form>
</fieldset>

</body>
</html>