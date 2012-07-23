<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Add New Update</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Add New Update</h1>

<fieldset>
<legend>New Update</legend>
<form:form modelAttribute="update" method="POST" enctype="multipart/form-data" commandName="update">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:input type="hidden" path="package_id" value="${packageId}"/>
	</p>
	<p>
		<form:label for="filedata" path="filedata">File:</form:label><br/>
        <form:input path="filedata" type="file"/>
	</p>
	<p>
		<form:label path="changelog" for="changelog">Changelog:</form:label>
		<form:textarea path="changelog" rows="10"/>
	</p>
	<p>
		<form:label path="version" for="version">Version:</form:label>
		<form:input path="version"/>
	</p>
	<p>
		<form:label path="type" for="type">Type:</form:label>
		<form:select path="type">
        <form:options items="${update.updateTypes}" />
		</form:select>
	</p>
	<p>
		<input name="send" type="submit" value="Send" />
	</p>
</form:form>
</fieldset>

</body>
</html>