package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.data.Dashboard
import de.misi.idea.plugins.jenkins.parser.data.HealthReport
import de.misi.idea.plugins.jenkins.parser.data.JobsHolder
import de.misi.idea.plugins.jenkins.parser.data.impl.JobImpl
import de.misi.idea.plugins.jenkins.parser.data.views.Hudson
import de.misi.idea.plugins.jenkins.parser.data.views.View
import de.misi.idea.plugins.jenkins.settings.Authentication
import de.misi.idea.plugins.jenkins.settings.PasswordManager
import de.misi.idea.plugins.jenkins.settings.URLSettings
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilderFactory

internal fun createJenkinsStateHandler(urlSettings: URLSettings) = try {
    JenkinsStateHandlerImpl(parseJobs(urlSettings))
} catch (e: Exception) {
    ErrorStateHandler(e.message!!)
}

internal fun parseJobs(urlSettings: URLSettings) = parseJobs(sendRequest(appendTreeParameters(urlSettings.url), urlSettings.authentication)).jobs

private fun appendTreeParameters(url: String) = url + "?tree=" + URLEncoder.encode("jobs[displayName,color,url,healthReport[description]]", "UTF-8")

internal fun parseJobs(inputStream: InputStream): JobsHolder {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
    val element = document.documentElement
    return if (element.nodeName == "dashboard") {
        Dashboard(element.textContent("name"), element.textContent("url"), element.textContent("description"), createJobs(element))
    } else {
        Hudson(createViews(element), createJobs(element))
    }
}

private fun createJobs(element: Element) = createChildElements(element, "job", ::createJob)

private fun createJob(item: Element) =
        JobImpl(
                item.textContent("displayName"),
                cleanupJobColor(item),
                item.textContent("url"),
                parseHealthReport(item.getElementByTagName("healthReport"))
        )

private fun cleanupJobColor(item: Element): String {
    val color = item.textContent("color", JobState.unknown.name)
    if (color.endsWith("_aborted")) {
        return "aborted"
    }
    return color.replace("_anime", "")
}

private fun Element.textContent(tagName: String) = textContent(tagName, "")

private fun Element.textContent(tagName: String, defaultValue: String) = getElementByTagName(tagName)?.textContent ?: defaultValue

private fun Element.getElementByTagName(tagName: String) = getElementsByTagName(tagName).getFirstElement()

private fun NodeList.getFirstElement() =
        if (length > 0) {
            item(0) as Element
        } else {
            null
        }

private fun <N : Node, T> NodeList.forEach(func: (N) -> T) =
        (0..length)
                .mapNotNull { item(it) }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    func(it as N)
                }

internal fun parseHudson(url: String, authentication: Authentication) = parseHudson(sendRequest(url, authentication))

internal fun parseHudson(inputStream: InputStream): Hudson {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
    val element = document.documentElement
    return Hudson(createViews(element), createJobs(element))
}

private fun createViews(element: Element) = createChildElements(element, "view", ::createView)

private fun <T> createChildElements(element: Element, tagName: String, func: (Element) -> T) =
        element.getElementsByTagName(tagName).forEach<Element, T> { func(it) }

private fun createView(item: Element) =
        View(item.textContent("_class"), item.textContent("name"), item.textContent("url"))

internal fun findJobState(state: String) = JobState.values().find { state.startsWith(it.name) } ?: JobState.unknown

private fun parseHealthReport(element: Element?) =
        HealthReport(element?.textContent("description") ?: "")

private fun sendRequest(url: String, authentication: Authentication): InputStream {
    val client = HttpClient()
    val method = GetMethod(url)
    if (authentication.required) {
        prepareForAuthentication(authentication, client, method, URL(url))
    }

    try {
        val response = client.executeMethod(method)
        if (response >= 400) {
            throw JenkinsParserException(method.statusText)
        }
        return method.responseBody.inputStream()
    } finally {
        method.releaseConnection()
    }
}

private fun prepareForAuthentication(authentication: Authentication, client: HttpClient, method: GetMethod, url: URL) {
    val username = authentication.username
    if (username?.isEmpty() != false) {
        throw JenkinsParserException("Authentication is required, but no username was set.")
    }
    val password = PasswordManager.readPassword(username)
    if (password?.isEmpty() != false) {
        throw JenkinsParserException("No API-Token for user $username available.")
    }
    client.state.setCredentials(
            AuthScope(url.host, url.port),
            UsernamePasswordCredentials(authentication.username, password)
    )
    client.params.isAuthenticationPreemptive = true
    method.doAuthentication = true
}

fun createRestURL(url: String): String {
    return url + (if (url.endsWith("/")) "" else "/") + "api/xml"
}
