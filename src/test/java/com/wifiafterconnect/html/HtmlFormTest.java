package com.wifiafterconnect.html;

import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URL;

/**
 * Created by brad on 5/11/15.
 */
public class HtmlFormTest extends TestCase {
    protected Document document = Jsoup.parse("<form></form>");
    protected HtmlForm empty_form = new HtmlForm(document.getElementsByTag("form").first());

    public HtmlForm formFromHtml(String html) {
        return new HtmlForm(Jsoup.parse(html).getElementsByTag("form").first());
    }

    public boolean isSubmittable(String html) {
        return formFromHtml(html).isSubmittable();
    }

    public void testHtmlForm() throws Exception {
        assertEquals("", empty_form.getId());
        assertEquals("", empty_form.getAction());
        assertEquals("", empty_form.getMethod());
        assertEquals("", empty_form.getOnsubmit());
        assertEquals(0, empty_form.getInputs().size());

        String html = "<form id=\"form_id\" action=\".\" method=\"POST\" onsubmit=\"javascript:alert('test')\"><input type=\"email\" name=\"email_name\"></form>";
        HtmlForm f = formFromHtml(html);
        assertEquals("form_id", f.getId());
        assertEquals(".", f.getAction());
        assertEquals("POST", f.getMethod());
        assertEquals("javascript:alert('test')", f.getOnsubmit());
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_EMAIL, f.getInput("email_name").getType());
        assertEquals(false, f.getInput("email_name").isHidden());

        html = "<form><div class=\"hidedata\"><input type=\"text\" name=\"text_name\"></div></form>";
        f = formFromHtml(html);
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_TEXT, f.getInput("text_name").getType());
        assertEquals(true, f.getInput("text_name").isHidden());

        html = "<form><div class=\"hidden\"><input type=\"text\" name=\"text_name\"></div></form>";
        f = formFromHtml(html);
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_TEXT, f.getInput("text_name").getType());
        assertEquals(true, f.getInput("text_name").isHidden());
    }

    public void testAddInput() throws Exception {
        assertTrue(empty_form.getInputs().isEmpty());
        empty_form.addInput(null);
        assertTrue(empty_form.getInputs().isEmpty());
        empty_form.addInput(new HtmlInput(null, null, null));
        assertTrue(empty_form.getInputs().isEmpty());
        empty_form.addInput(new HtmlInput("", null, null));
        assertTrue(empty_form.getInputs().isEmpty());
        empty_form.addInput(new HtmlInput("test_name", HtmlInput.TYPE_BUTTON, null));
        assertEquals(1, empty_form.getInputs().size());
        assertEquals("button", empty_form.getInput("test_name").getType());
    }

    public void testGetOnsubmit() throws Exception {
        assertEquals("", empty_form.getOnsubmit());
        HtmlForm f = formFromHtml("<form onsubmit=\"javascript:alert('test')\"></form>");
        assertEquals("javascript:alert('test')", f.getOnsubmit());
        f = formFromHtml("<form onsubmit=\"ANYTHING AT ALL\"></form>");
        assertEquals("ANYTHING AT ALL", f.getOnsubmit());
    }

    public void testGetMethod() throws Exception {
        assertEquals("", empty_form.getMethod());
        HtmlForm f = formFromHtml("<form method=\"get\"></form>");
        assertEquals("GET", f.getMethod());
        f = formFromHtml("<form method=\"GET\"></form>");
        assertEquals("GET", f.getMethod());
        f = formFromHtml("<form method=\"gEt\"></form>");
        assertEquals("GET", f.getMethod());
        f = formFromHtml("<form method=\"post\"></form>");
        assertEquals("POST", f.getMethod());
        f = formFromHtml("<form method=\"fakemethod\"></form>");
        assertEquals("FAKEMETHOD", f.getMethod());
    }

    public void testGetAction() throws Exception {
        assertEquals("", empty_form.getAction());
        empty_form.setAction(".");
        assertEquals(".", empty_form.getAction());
        empty_form.setAction("submit.asp");
        assertEquals("submit.asp", empty_form.getAction());
        empty_form.setAction("/submit/");
        assertEquals("/submit/", empty_form.getAction());
        empty_form.setAction("http://example.com/submit/");
        assertEquals("http://example.com/submit/", empty_form.getAction());
        empty_form.setAction("GOBBLEDIGOOK");
        assertEquals("GOBBLEDIGOOK", empty_form.getAction());
    }

    public void testGetId() throws Exception {
        assertEquals("", empty_form.getId());
        HtmlForm f = formFromHtml("<form id=\"form_id\"></form>");
        assertEquals("form_id", f.getId());
        f = formFromHtml("<form id=\"ANYTHING AT ALL\"></form>");
        assertEquals("ANYTHING AT ALL", f.getId());
    }

    public void testGetInputs() throws Exception {
        assertTrue(empty_form.getInputs().isEmpty());
        empty_form.addInput(new HtmlInput("test_name", "datetime", null));
        assertEquals(1, empty_form.getInputs().size());
        assertEquals("datetime", ((HtmlInput) empty_form.getInputs().toArray()[0]).getType());
    }

    public void testHasInput() throws Exception {
        HtmlForm f = formFromHtml("<form><input type=\"text\" name=\"text_name\"></form>");
        assertTrue(f.hasInput("text_name"));
        assertFalse(f.hasInput("fake_name"));
    }

    public void testHasInputWithClass() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\" class=\"text_class\"></form>";
        HtmlForm f = formFromHtml(html);
        assertTrue(f.hasInputWithClass("text_class"));
        assertFalse(f.hasInputWithClass("fake_class"));
    }

    public void testHasVisibleInput() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\"><div class=\"hidden\"><input type=\"text\" name=\"hidden1\"></div><input type=\"hidden\" name=\"hidden2\"></form>";
        HtmlForm f = formFromHtml(html);
        assertTrue(f.hasVisibleInput("text_name"));
        assertFalse(f.hasVisibleInput("hidden1"));
        assertFalse(f.hasVisibleInput("hidden2"));
        assertFalse(f.hasVisibleInput("fake_name"));
    }

    public void testGetInput() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\" class=\"text_class\"></form>";
        HtmlForm f = formFromHtml(html);
        HtmlInput i = (HtmlInput) f.getInputs().toArray()[0];
        assertEquals(i.getType(), f.getInput("text_name").getType());
        assertEquals(i.getName(), f.getInput("text_name").getName());
        assertEquals(null, f.getInput("fake_name"));
    }

    public void testGetVisibleInput() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\"><div class=\"hidden\"><input type=\"text\" name=\"hidden1\"></div><input type=\"hidden\" name=\"hidden2\"></form>";
        HtmlForm f = formFromHtml(html);
        assertEquals(f.getInput("text_name"), f.getVisibleInput("text_name"));
        assertEquals(null, f.getVisibleInput("hidden1"));
        assertEquals(null, f.getVisibleInput("hidden2"));
        assertEquals(null, f.getVisibleInput("fake_name"));
    }

    public void testGetVisibleInputByType() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\"><input type=\"text\" name=\"text_name2\"><div class=\"hidden\"><input type=\"image\" name=\"image_name\"></div><input type=\"hidden\" name=\"hidden_name\"><input type=\"datetime\" name=\"datetime_name\"></form>";
        HtmlForm f = formFromHtml(html);
        // Ihere are multiple text inputs, we can't predict which will be returned
        String visible_text = f.getVisibleInputByType("text").getName();
        assertTrue(visible_text.equals("text_name") || visible_text.equals("text_name2"));
        assertEquals(f.getInput("datetime_name"), f.getVisibleInputByType("datetime"));
        assertEquals(null, f.getVisibleInputByType("image"));
        assertEquals(null, f.getVisibleInputByType("hidden"));
    }

    public void testSetInputValue() throws Exception {
        String html = "<form><input type=\"text\" name=\"text_name\"><input type=\"hidden\" name=\"hidden_name\"></form>";
        HtmlForm f = formFromHtml(html);
        HtmlInput i = f.getInput("text_name");
        assertEquals("", i.getValue());
        assertEquals(true, f.setInputValue("text_name", "new_value"));
        assertEquals("new_value", f.getInput("text_name").getValue());
        i = f.getInput("hidden_name");
        assertEquals("", i.getValue());
        assertEquals(true, f.setInputValue("hidden_name", "hidden_value"));
        assertEquals("hidden_value", f.getInput("hidden_name").getValue());
        assertEquals(false, f.setInputValue("fake_name", "fake_value"));
    }

    public void testFormatPostData() throws Exception {
        String html = "<form><input type=\"submit\" name=\"ctl00$ContentPlaceHolder1$submit\" value=\" \"></form>";
        String postData = formFromHtml(html).formatPostData();
        assertEquals("ctl00%24ContentPlaceHolder1%24submit=+", postData);
        html = "<form><input type=\"image\" name=\"image_name\" value=\"image_value\"></form>";
        postData = formFromHtml(html).formatPostData();
        assertEquals("x=1&y=1", postData);
    }

    public void testFormatActionURL() throws Exception {
        URL url = new URL("http://example.com");
        assertEquals(url, empty_form.formatActionURL(url));

        HtmlForm f = formFromHtml("<form action=\"http://example.com\"></form>");
        assertEquals(url, f.formatActionURL(url));

        f = formFromHtml("<form action=\"http://example.com/file/\"></form>");
        assertEquals(new URL("http://example.com/file/"), f.formatActionURL(url));

        f = formFromHtml("<form action=\"./file/\"></form>");
        assertEquals(new URL("http://example.com/file/"), f.formatActionURL(url));

        // TODO: Write code to make this test pass!
//        f = formFromHtml("<form action=\"file/\"></form>");
//        assertEquals(new URL("http://example.com/file/"), f.formatActionURL(url));

        // TODO: Write code to make this test pass!
//        f = formFromHtml("<form action=\"file.asp\"></form>");
//        assertEquals(new URL("http://example.com/file.asp"), f.formatActionURL(url));

        f = formFromHtml("<form action=\"../file1/\"></form>");
        URL url2 = new URL("http://example.com/file2/");
        assertEquals(new URL("http://example.com/file2/../file1/"), f.formatActionURL(url2));

        f = formFromHtml("<form action=\"http://example.com/#ref\"></form>");
        assertEquals(new URL("http://example.com/#ref"), f.formatActionURL(url));
    }

    public void testFillParams() throws Exception {

    }

    public void testIsParamMissing() throws Exception {

    }

    public void testFillInputs() throws Exception {

    }

    public void testIsSubmittable() throws Exception {
        assertFalse(empty_form.isSubmittable());

        String html = "<form><input type=\"submit\"></form>";
        // TODO: is this right? I think nameless submits are quite common
        // Still not submittable because the submit input has no name.
        assertFalse(isSubmittable(html));
        html = "<form><input type=\"submit\" value=\"submit_value\"></form>";
        assertFalse(isSubmittable(html));

        html = "<form><input type=\"submit\" name=\"submit_name\"></form>";
        assertTrue(isSubmittable(html));

        html = "<form><input type=\"checkbox\" name=\"checkbox_name\" value=\"checkbox_value\"><input type=\"submit\" name=\"submit_name\"></form>";
        assertTrue(isSubmittable(html));

        html = "<form><input type=\"checkbox\" name=\"checkbox_name\" value=\"\"><input type=\"submit\" name=\"submit_name\"></form>";
        assertFalse(isSubmittable(html));

        html = "<form><input type=\"checkbox\" name=\"checkbox_name\"><input type=\"submit\" name=\"submit_name\"></form>";
        assertFalse(isSubmittable(html));

        html = "<form><input type=\"text\" name=\"text_name\"><input type=\"submit\" name=\"submit_name\"></form>";
        assertFalse(isSubmittable(html));

        html = "<form><input type=\"text\" name=\"text_name\" value=\"text_value\"><input type=\"submit\" name=\"submit_name\"></form>";
        assertTrue(isSubmittable(html));

        html = "<form><input type=\"text\" name=\"text_name\" value=\"text_value\"></form>";
        assertFalse(isSubmittable(html));
    }

    public void testSetAction() throws Exception {
        assertEquals("", empty_form.getAction());
        empty_form.setAction("http://example.com");
        assertEquals("http://example.com", empty_form.getAction());
        empty_form.setAction(null);
        assertEquals("", empty_form.getAction());
        empty_form.setAction("http://example.com/file#ref");
        assertEquals("http://example.com/file#ref", empty_form.getAction());
        // TODO: Evaluate whether action should be set to "" here
        empty_form.setAction("MALFORMED_URL");
        assertEquals("MALFORMED_URL", empty_form.getAction());
    }
}