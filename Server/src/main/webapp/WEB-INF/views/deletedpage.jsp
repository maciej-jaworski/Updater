<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Date" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>User Deleted Confirmation</title>
</head>
<body>
 
<h1>Users</h1>
 
<p>You have deleted a user with id ${id} at <%= new java.util.Date() %></p>
 
<c:url var="mainUrl" value="/users" />
<p>Return to <a href="${mainUrl}">Main List</a></p>
 
</body>
</html>