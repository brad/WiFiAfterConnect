package com.wifiafterconnect.html;

import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by brad on 5/11/15.
 */
public class HtmlFormTest extends TestCase {

    public void testHtmlForm() throws Exception {
        Document doc = Jsoup.parse("<form></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        assertEquals("", f.getId());
        assertEquals("", f.getAction());
        assertEquals("", f.getMethod());
        assertEquals("", f.getOnsubmit());
        assertEquals(0, f.getInputs().size());

        doc = Jsoup.parse("<form id=\"form_id\" action=\".\" method=\"POST\" onsubmit=\"javascript:alert('test')\"><input type=\"email\" name=\"email_name\"></form>");
        f = new HtmlForm(doc.getElementsByTag("form").first());
        assertEquals("form_id", f.getId());
        assertEquals(".", f.getAction());
        assertEquals("POST", f.getMethod());
        assertEquals("javascript:alert('test')", f.getOnsubmit());
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_EMAIL, f.getInput("email_name").getType());
        assertEquals(false, f.getInput("email_name").isHidden());

        doc = Jsoup.parse("<form><div class=\"hidedata\"><input type=\"text\" name=\"text_name\"></div></form>");
        f = new HtmlForm(doc.getElementsByTag("form").first());
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_TEXT, f.getInput("text_name").getType());
        assertEquals(true, f.getInput("text_name").isHidden());

        doc = Jsoup.parse("<form><div class=\"hidden\"><input type=\"text\" name=\"text_name\"></div></form>");
        f = new HtmlForm(doc.getElementsByTag("form").first());
        assertEquals(1, f.getInputs().size());
        assertEquals(HtmlInput.TYPE_TEXT, f.getInput("text_name").getType());
        assertEquals(true, f.getInput("text_name").isHidden());
    }

    public void testAddInput() throws Exception {
    }

    public void testGetOnsubmit() throws Exception {

    }

    public void testGetMethod() throws Exception {

    }

    public void testGetAction() throws Exception {

    }

    public void testGetId() throws Exception {

    }

    public void testGetInputs() throws Exception {

    }

    public void testHasInput() throws Exception {
        Document doc = Jsoup.parse("<form><input type=\"text\" name=\"text_name\"></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        assertTrue(f.hasInput("text_name"));
        assertFalse(f.hasInput("fake_name"));
    }

    public void testHasInputWithClass() throws Exception {
        Document doc = Jsoup.parse("<form><input type=\"text\" name=\"text_name\" class=\"text_class\"></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        assertTrue(f.hasInputWithClass("text_class"));
        assertFalse(f.hasInputWithClass("fake_class"));
    }

    public void testHasVisibleInput() throws Exception {
        Document doc = Jsoup.parse("<form><input type=\"text\" name=\"text_name\"><div class=\"hidden\"><input type=\"text\" name=\"hidden1\"></div><input type=\"hidden\" name=\"hidden2\"></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        assertTrue(f.hasVisibleInput("text_name"));
        assertFalse(f.hasVisibleInput("hidden1"));
        assertFalse(f.hasVisibleInput("hidden2"));
        assertFalse(f.hasVisibleInput("fake_name"));
    }

    public void testGetInput() throws Exception {
        Document doc = Jsoup.parse("<form><input type=\"text\" name=\"text_name\" class=\"text_class\"></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        HtmlInput i = new HtmlInput(doc.getElementsByTag("input").first(), false);
        assertEquals(i.getType(), f.getInput("text_name").getType());
        assertEquals(i.getName(), f.getInput("text_name").getName());
        assertEquals(null, f.getInput("fake_name"));
    }

    public void testGetVisibleInput() throws Exception {
        Document doc = Jsoup.parse("<form><input type=\"text\" name=\"text_name\"><div class=\"hidden\"><input type=\"text\" name=\"hidden1\"></div><input type=\"hidden\" name=\"hidden2\"></form>");
        HtmlForm f = new HtmlForm(doc.getElementsByTag("form").first());
        assertEquals(f.getInput("text_name"), f.getVisibleInput("text_name"));
        assertEquals(null, f.getVisibleInput("hidden1"));
        assertEquals(null, f.getVisibleInput("hidden2"));
        assertEquals(null, f.getVisibleInput("fake_name"));
    }

    public void testGetVisibleInputByType() throws Exception {

    }

    public void testSetInputValue() throws Exception {

    }

    public void testFormatPostData() throws Exception {

    }

    public void testFormatActionURL() throws Exception {

    }

    public void testFillParams() throws Exception {

    }

    public void testIsParamMissing() throws Exception {

    }

    public void testFillInputs() throws Exception {

    }

    public void testIsSubmittable() throws Exception {

    }

    public void testSetAction() throws Exception {

    }
}