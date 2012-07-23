<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Edit Update</title>
</head>
<body>
 
<h1>Edit Update Info</h1>
 
<c:url var="saveUrl" value="/main/updates/edit?id=${update.id}" />
<fieldset>
<legend>Edit Update Info</legend>
<form:form modelAttribute="update" method="POST" enctype="multipart/form-data" commandName="update">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:input type="hidden" path="package_id" value="${update.package_id}"/>
	</p>
	<p>
		<form:label path="changelog" for="changelog">Changelog:</form:label>
		<form:textarea path="changelog" rows="10" value="${update.changelog}" />
	</p>
	<p>
		<form:label path="version" for="version">Version:</form:label>
		<form:input path="version" value="${update.version}"/>
	</p>
	<p>
		<input name="send" type="submit" value="Send" />
	</p>
</form:form>
</fieldset>
 
</body>
</html>