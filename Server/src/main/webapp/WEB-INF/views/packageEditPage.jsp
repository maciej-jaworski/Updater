<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Edit Package</title>
</head>
<body>
 
<h1>Edit Package Data</h1>
 
<c:url var="saveUrl" value="/main/packages/edit?id=${pack.id}" />
<fieldset>
<legend>Edit Package</legend>
<form:form modelAttribute="pack" method="POST" commandName="pack">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:label path="name" for="name">Package Name:</form:label>
		<form:input path="name" value="${pack.name}"/>
	</p>
	<p>
		<form:input type="hidden" path="programId" value="${programId}"/>
	</p>
	<p>
		<input name="send" type="submit" value="Save" />
	</p>
</form:form>
</fieldset>
 
</body>
</html>