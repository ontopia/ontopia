<%
/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/html/portlet/wiki/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String originalRedirect = ParamUtil.getString(request, "originalRedirect", StringPool.BLANK);

if (originalRedirect.equals(StringPool.BLANK)) {
	originalRedirect = redirect;
}
else {
	redirect = originalRedirect;
}

boolean followRedirect = false;

WikiNode node = (WikiNode)request.getAttribute(WebKeys.WIKI_NODE);
WikiPage wikiPage = (WikiPage)request.getAttribute(WebKeys.WIKI_PAGE);

WikiPage redirectPage = null;

long nodeId = BeanParamUtil.getLong(wikiPage, request, "nodeId");
String title = BeanParamUtil.getString(wikiPage, request, "title");

boolean editTitle = ParamUtil.getBoolean(request, "editTitle");

String content = BeanParamUtil.getString(wikiPage, request, "content");
String format = BeanParamUtil.getString(wikiPage, request, "format", WikiPageImpl.DEFAULT_FORMAT);
String parentTitle = BeanParamUtil.getString(wikiPage, request, "parentTitle");

String[] attachments = new String[0];

boolean preview = ParamUtil.getBoolean(request, "preview");

boolean newPage = false;

if (wikiPage == null) {
	newPage = true;
}

boolean editable = false;

if (wikiPage != null) {
	attachments = wikiPage.getAttachmentsFiles();

	editable = true;
}
else if (Validator.isNotNull(title)) {
	try {
		WikiPageLocalServiceUtil.validateTitle(title);

		editable = true;
	}
	catch (PortalException pe) {
	}
}
else if ((wikiPage == null) && editTitle) {
	editable = true;

	wikiPage = new WikiPageImpl();

	wikiPage.setNew(true);
	wikiPage.setNodeId(node.getNodeId());
	wikiPage.setFormat(format);
	wikiPage.setParentTitle(parentTitle);
}

long templateNodeId = ParamUtil.getLong(request, "templateNodeId");
String templateTitle = ParamUtil.getString(request, "templateTitle");

WikiPage templatePage = null;

if ((templateNodeId > 0) && Validator.isNotNull(templateTitle)) {
	try {
		templatePage = WikiPageServiceUtil.getPage(templateNodeId, templateTitle);

		if (Validator.isNull(parentTitle)) {
			parentTitle = templatePage.getParentTitle();

			if (wikiPage.isNew()) {
				format = templatePage.getFormat();

				wikiPage.setContent(templatePage.getContent());
				wikiPage.setFormat(format);
				wikiPage.setParentTitle(parentTitle);
			}
		}
	}
	catch (Exception e) {
	}
}

PortletURL viewPageURL = renderResponse.createRenderURL();

viewPageURL.setParameter("struts_action", "/wiki/view");
viewPageURL.setParameter("nodeName", node.getName());
viewPageURL.setParameter("title", title);

PortletURL editPageURL = renderResponse.createRenderURL();

editPageURL.setParameter("struts_action", "/wiki/edit_page");
editPageURL.setParameter("redirect", currentURL);
editPageURL.setParameter("nodeId", String.valueOf(node.getNodeId()));
editPageURL.setParameter("title", title);

if (Validator.isNull(redirect)) {
	redirect = viewPageURL.toString();
}
%>

<liferay-util:include page="/html/portlet/wiki/top_links.jsp" />

<c:choose>
	<c:when test="<%= !newPage %>">
		<liferay-util:include page="/html/portlet/wiki/page_tabs.jsp">
			<liferay-util:param name="tabs1" value="content" />
		</liferay-util:include>
	</c:when>
	<c:otherwise>
		<%@ include file="/html/portlet/wiki/page_name.jspf" %>
	</c:otherwise>
</c:choose>

<c:if test="<%= preview %>">

	<%
	if (wikiPage == null) {
		wikiPage = new WikiPageImpl();
	}

	wikiPage.setContent(content);
	wikiPage.setFormat(format);
	%>

	<liferay-ui:message key="preview" />:

	<div class="preview">
		<%@ include file="/html/portlet/wiki/view_page_content.jspf" %>
	</div>

	<br />
</c:if>

<script type="text/javascript">
	function <portlet:namespace />changeFormat(formatSel) {
		if (window.<portlet:namespace />editor) {
			document.<portlet:namespace />fm.<portlet:namespace />content.value = window.<portlet:namespace />editor.getHTML();
		}

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />getSuggestionsContent() {
		var content = '';

		content += document.<portlet:namespace />fm.<portlet:namespace/>title.value + ' ';
		content += document.<portlet:namespace />fm.<portlet:namespace />content.value;

		return content;
	}

	function <portlet:namespace />previewPage() {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "";
		document.<portlet:namespace />fm.<portlet:namespace />preview.value = "true";

		if (window.<portlet:namespace />editor) {
			document.<portlet:namespace />fm.<portlet:namespace />content.value = window.<portlet:namespace />editor.getHTML();
		}

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />savePage() {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= newPage ? Constants.ADD : Constants.UPDATE %>";

		if (window.<portlet:namespace />editor) {
			document.<portlet:namespace />fm.<portlet:namespace />content.value = window.<portlet:namespace />editor.getHTML();
		}

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />saveAndContinuePage() {
		document.<portlet:namespace />fm.<portlet:namespace />saveAndContinue.value = "true";
		<portlet:namespace />savePage();
	}
</script>

<form action="<portlet:actionURL><portlet:param name="struts_action" value="/wiki/edit_page" /></portlet:actionURL>" method="post" name="<portlet:namespace />fm" onSubmit="<portlet:namespace />savePage(); return false;">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<input name="<portlet:namespace />redirect" type="hidden" value="<%= HtmlUtil.escape(redirect) %>" />
<input name="<portlet:namespace />originalRedirect" type="hidden" value="<%= HtmlUtil.escape(originalRedirect) %>" />
<input name="<portlet:namespace />nodeId" type="hidden" value="<%= nodeId %>" />

<c:if test="<%= !editTitle %>">
	<input name="<portlet:namespace />title" type="hidden" value="<%= title %>" />
</c:if>

<input name="<portlet:namespace />parentTitle" type="hidden" value="<%= parentTitle %>" />
<input name="<portlet:namespace />editTitle" type="hidden" value="<%= editTitle %>" />

<c:if test="<%= wikiPage != null %>">
	<input name="<portlet:namespace />version" type="hidden" value="<%= wikiPage.getVersion() %>" />
</c:if>

<input name="<portlet:namespace />preview" type="hidden" value="<%= preview %>" />
<input name="<portlet:namespace />saveAndContinue" type="hidden" value="" />

<liferay-ui:error exception="<%= DuplicatePageException.class %>" message="there-is-already-a-page-with-the-specified-title" />
<liferay-ui:error exception="<%= PageContentException.class %>" message="the-content-is-not-valid" />
<liferay-ui:error exception="<%= PageTitleException.class %>" message="please-enter-a-valid-title" />
<liferay-ui:error exception="<%= PageVersionException.class %>" message="another-user-has-made-changes-since-you-started-editing-please-copy-your-changes-and-try-again" />
<liferay-ui:tags-error />

<c:if test="<%= newPage %>">
	<c:choose>
		<c:when test="<%= editable %>">
			<div class="portlet-msg-info">
				<liferay-ui:message key="this-page-does-not-exist-yet-use-the-form-below-to-create-it" />
			</div>
		</c:when>
		<c:otherwise>
			<div class="portlet-msg-error">
				<liferay-ui:message key="this-page-does-not-exist-yet-and-the-title-is-not-valid" />
			</div>

			<input type="button" value="<liferay-ui:message key="cancel" />" onClick="document.location = '<%= HtmlUtil.escape(redirect) %>'" />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="<%= editable %>">
	<table class="lfr-table">

	<c:if test="<%= editTitle %>">
		<tr>
			<td class="lfr-label">
				<liferay-ui:message key="title" />
			</td>
			<td>
				<input name="<portlet:namespace />title" size="30" type="text" value="<%= title %>" />
			</td>
		</tr>
	</c:if>

	<c:if test="<%= Validator.isNotNull(parentTitle) %>">
		<tr>
			<td>
				<liferay-ui:message key="parent" />
			</td>
			<td>
				<%= parentTitle %>
			</td>
		</tr>
	</c:if>

	<c:choose>
		<c:when test="<%= (WikiPageImpl.FORMATS.length > 1) %>">
			<tr>
				<td class="lfr-label">
					<liferay-ui:message key="format" />
				</td>
				<td>
					<select name="<portlet:namespace />format" onChange="<portlet:namespace />changeFormat(this);">

						<%
						for (int i = 0; i < WikiPageImpl.FORMATS.length; i++) {
						%>

							<option <%= format.equals(WikiPageImpl.FORMATS[i]) ? "selected" : "" %> value="<%= WikiPageImpl.FORMATS[i] %>"><%= LanguageUtil.get(pageContext, "wiki.formats." + WikiPageImpl.FORMATS[i]) %></option>

						<%
						}
						%>

					</select>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<input name="<portlet:namespace />format" type="hidden" value="<%= format %>" />
		</c:otherwise>
	</c:choose>

	</table>

	<br />

	<div>

		<%
		request.setAttribute("edit_page.jsp-wikiPage", wikiPage);
		%>

		<liferay-util:include page="<%= WikiUtil.getEditPage(format) %>" />
	</div>

	<br />

	<table class="lfr-table">

	<c:if test="<%= attachments.length > 0 %>">
		<tr>
			<td>
				<liferay-ui:message key="attachments" />
			</td>
			<td>

				<%
				for (int i = 0; i < attachments.length; i++) {
					String fileName = FileUtil.getShortFileName(attachments[i]);
					long fileSize = DLServiceUtil.getFileSize(company.getCompanyId(), CompanyConstants.SYSTEM, attachments[i]);
				%>

					<a href="<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/wiki/get_page_attachment" /><portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" /><portlet:param name="title" value="<%= wikiPage.getTitle() %>" /><portlet:param name="fileName" value="<%= fileName %>" /></portlet:actionURL>"><%= fileName %></a> (<%= TextFormatter.formatKB(fileSize, locale) %>k)<%= (i < (attachments.length - 1)) ? ", " : "" %>

				<%
				}
				%>

			</td>
		</tr>
		<tr>
			<td colspan="2">
				<br />
			</td>
		</tr>
	</c:if>

	<%
	long classPK = 0;

	if (!newPage) {
		classPK = wikiPage.getResourcePrimKey();
	}
	else if (Validator.isNotNull(templatePage)) {
		classPK = templatePage.getResourcePrimKey();
	}
	%>

	<tr>
		<td>
			<liferay-ui:message key="categories" />
		</td>
		<td>
			<liferay-ui:tags-selector
				className="<%= WikiPage.class.getName() %>"
				classPK="<%= classPK %>"
				folksonomy="<%= false %>"
				hiddenInput="tagsCategories"
			/>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<br />
		</td>
	</tr>
	<tr>
		<td class="lfr-label">
			<liferay-ui:message key="tags" />
		</td>
		<td>
			<liferay-ui:tags-selector
				className="<%= WikiPage.class.getName() %>"
				classPK="<%= classPK %>"
				hiddenInput="tagsEntries"
				contentCallback='<%= renderResponse.getNamespace() + "getSuggestionsContent" %>'
			/>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<br />
		</td>
	</tr>
	<tr>
		<td class="lfr-label">
			<liferay-ui:message key="summary" />
		</td>
		<td>
			<liferay-ui:input-field model="<%= WikiPage.class %>" bean="<%= wikiPage %>" field="summary" />
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<br />
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<liferay-ui:input-field model="<%= WikiPage.class %>" bean="<%= wikiPage %>" field="minorEdit" />

			<liferay-ui:message key="this-is-a-minor-edit" />
		</td>
	</tr>
	</table>

	<br />

	<input type="submit" value="<liferay-ui:message key="save" />" />

	<input type="button" value="<liferay-ui:message key="save-and-continue" />" onClick="<portlet:namespace />saveAndContinuePage();" />

	<input type="button" value="<liferay-ui:message key="preview" />" onClick="<portlet:namespace />previewPage();" />

	<input type="button" value="<liferay-ui:message key="cancel" />" onClick="document.location = '<%= HtmlUtil.escape(redirect) %>'" />

	</form>

	<c:if test="<%= !preview %>">
		<script type="text/javascript">
			if (!window.<portlet:namespace />editor) {
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= editTitle ? "title" : "content" %>);
			}
		</script>
	</c:if>
</c:if>