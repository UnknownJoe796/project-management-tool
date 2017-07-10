package com.ivieleague.kotlin

data class ElementType(
        val shortName: String,
        val kotlinName: String,
        val htmlName: String,
        val kotlinParent: String = "HTMLElement"
)

val elementTypes = listOf(

        //Control elements
        ElementType("html", "HTMLHtmlElement", "html"),
        ElementType("head", "HTMLHeadElement", "head"),
        ElementType("link", "HTMLLinkElement", "link", "HTMLHeadElement"),
        ElementType("meta", "HTMLMetaElement", "meta", "HTMLHeadElement"),
        ElementType("style", "HTMLStyleElement", "style", "HTMLHeadElement"),
        ElementType("body", "HTMLBodyElement", "body"),
        ElementType("base", "HTMLBaseElement", "base"),
        ElementType("script", "HTMLScriptElement", "script"),

        //Block elements
        ElementType("article", "HTMLElement", "article"),
        ElementType("aside", "HTMLElement", "aside"),
        ElementType("div", "HTMLDivElement", "div"),
        ElementType("details", "HTMLElement", "details"),
        ElementType("figure", "HTMLElement", "figure"),
        ElementType("figureCaption", "HTMLElement", "figcaption"),
        ElementType("footer", "HTMLElement", "footer"),
        ElementType("header", "HTMLElement", "header"),
        ElementType("main", "HTMLElement", "main"),
        ElementType("nav", "HTMLElement", "nav"),
        ElementType("section", "HTMLElement", "section"),
        ElementType("time", "HTMLElement", "time"),
        ElementType("title", "HTMLElement", "title"),

        //Plain text elements
        ElementType("heading1", "HTMLHeadingElement", "h1"),
        ElementType("heading2", "HTMLHeadingElement", "h2"),
        ElementType("heading3", "HTMLHeadingElement", "h3"),
        ElementType("heading4", "HTMLHeadingElement", "h4"),
        ElementType("heading5", "HTMLHeadingElement", "h5"),
        ElementType("heading6", "HTMLHeadingElement", "h6"),
        ElementType("paragraph", "HTMLParagraphElement", "p"),
        ElementType("blockQuote", "HTMLElement", "blockquote"),
        ElementType("br", "HTMLElement", "br"),
        ElementType("cite", "HTMLElement", "cite"),
        ElementType("code", "HTMLElement", "code"),
        ElementType("keyboardInput", "HTMLElement", "kbd"),
        ElementType("quote", "HTMLElement", "q"),
        ElementType("sample", "HTMLElement", "samp"),
        ElementType("summary", "HTMLElement", "summary"),

        //interactive elements
        ElementType("anchor", "HTMLAnchorElement", "a"),
        ElementType("audio", "HTMLAudioElement", "audio"),
        ElementType("source", "HTMLSourceElement", "source", "HTMLAudioElement"),
        ElementType("track", "HTMLTrackElement", "track", "HTMLAudioElement"),
        ElementType("video", "HTMLVideoElement", "video"),
        ElementType("source", "HTMLSourceElement", "source", "HTMLVideoElement"),
        ElementType("track", "HTMLTrackElement", "track", "HTMLVideoElement"),
        ElementType("button", "HTMLButtonElement", "button"),
        ElementType("canvas", "HTMLCanvasElement", "canvas"),
        ElementType("dialog", "HTMLDialogElement", "dialog"),
        ElementType("embed", "HTMLEmbedElement", "embed"),
        ElementType("form", "HTMLFormElement", "form"),
        ElementType("fieldSet", "HTMLFieldSetElement", "fieldset", "HTMLFormElement"),
        ElementType("legend", "HTMLLegendElement", "legend", "HTMLFieldSetElement"),
        ElementType("iFrame", "HTMLIFrameElement", "iframe"),
        ElementType("img", "HTMLImageElement", "img"),
        ElementType("input", "HTMLInputElement", "input"),
        ElementType("keygen", "HTMLKeygenElement", "keygen"),
        ElementType("label", "HTMLLabelElement", "label"),
        ElementType("meter", "HTMLMeterElement", "meter"),
        ElementType("embeddedObject", "HTMLObjectElement", "object"),
        ElementType("param", "HTMLParamElement", "param", "HTMLObjectElement"),
        ElementType("output", "HTMLOutputElement", "output"),
        ElementType("textArea", "HTMLTextAreaElement", "textarea"),

        //Menus
        ElementType("menu", "HTMLMenuElement", "menu"),
        ElementType("item", "HTMLMenuItemElement", "menuitem", "HTMLMenuElement"),

        //Select
        ElementType("select", "HTMLSelectElement", "select"),
        ElementType("option", "HTMLOptionElement", "option", "HTMLSelectElement"),
        ElementType("group", "HTMLOptGroupElement", "optgroup", "HTMLSelectElement"),
        ElementType("option", "HTMLOptionElement", "option", "HTMLOptGroupElement"),


        //Lists
        ElementType("orderedList", "HTMLOListElement", "ol"),
        ElementType("item", "HTMLElement", "li", "HTMLOListElement"),
        ElementType("unorderedList", "HTMLUListElement", "ul"),
        ElementType("item", "HTMLElement", "li", "HTMLUListElement"),

        //Post-element
        ElementType("dataList", "HTMLElement", "datalist"),
        ElementType("map", "HTMLMapElement", "map"),
        ElementType("area", "HTMLAreaElement", "area", "HTMLMapElement"),

        //Tables
        ElementType("table", "HTMLTableElement", "table"),
        ElementType("body", "HTMLTableSectionElement", "tbody", "HTMLTableElement"),
        ElementType("foot", "HTMLTableSectionElement", "tfoot", "HTMLTableElement"),
        ElementType("head", "HTMLTableSectionElement", "thead", "HTMLTableElement"),
        ElementType("row", "HTMLTableRowElement", "tr", "HTMLTableElement"),
        ElementType("row", "HTMLTableRowElement", "tr", "HTMLTableSectionElement"),
        ElementType("header", "HTMLTableElement", "tr", "HTMLTableRowElement"),
        ElementType("cell", "HTMLTableCellElement", "td", "HTMLTableRowElement"),
        ElementType("columnGroup", "HTMLTableColElement", "colgroup", "HTMLTableElement"),
        ElementType("column", "HTMLTableColElement", "col", "HTMLTableColElement"),
        ElementType("caption", "HTMLTableCaptionElement", "caption", "HTMLTableElement"),

        //Data lists
        ElementType("definitionList", "HTMLDListElement", "dl"),
        ElementType("term", "HTMLElement", "dt", "HTMLDListElement"),
        ElementType("definition", "HTMLElement", "dd", "HTMLDListElement")


)

fun main(vararg args: String) {
    elementTypes.forEach {
        with(it) {
            println("inline fun $kotlinParent.$shortName(setup:$kotlinName.()->Unit) = (document.createElement(\"$htmlName\") as $kotlinName).apply(setup).also{ this.appendChild(it) }")
        }
    }
}