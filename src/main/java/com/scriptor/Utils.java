package com.scriptor;

import java.awt.Color;
import java.io.File;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

public class Utils {
    public static String getVersion() {
        return "2024.10.20-1";
    }

    public static String getSyntaxConstantByFileExtension(String extension) {
        switch (extension) {
            case "as":
                return SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT;
            case "asm":
            case "s":
            case "inc":
                return SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86;
            case "c":
                return SyntaxConstants.SYNTAX_STYLE_C;
            case "clj":
            case "cljs":
            case "cljc":
                return SyntaxConstants.SYNTAX_STYLE_CLOJURE;
            case "cpp":
                return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
            case "cs":
                return SyntaxConstants.SYNTAX_STYLE_CSHARP;
            case "css":
                return SyntaxConstants.SYNTAX_STYLE_CSS;
            case "d":
                return SyntaxConstants.SYNTAX_STYLE_D;
            case "dart":
                return SyntaxConstants.SYNTAX_STYLE_DART;
            case "f90":
            case "for":
            case "f":
                return SyntaxConstants.SYNTAX_STYLE_FORTRAN;
            case "go":
                return SyntaxConstants.SYNTAX_STYLE_GO;
            case "groovy":
                return SyntaxConstants.SYNTAX_STYLE_GROOVY;
            case "htm":
            case "html":
            case "ejs":
                return SyntaxConstants.SYNTAX_STYLE_HTML;
            case "ini":
            case "properties":
            case "prop":
            case "config":
                return SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
            case "java":
                return SyntaxConstants.SYNTAX_STYLE_JAVA;
            case "js":
            case "mjs":
            case "cjs":
                return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
            case "json":
                return SyntaxConstants.SYNTAX_STYLE_JSON;
            case "jsonc":
                return SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS;
            case "kt":
                return SyntaxConstants.SYNTAX_STYLE_KOTLIN;
            case "tex":
                return SyntaxConstants.SYNTAX_STYLE_LATEX;
            case "less":
                return SyntaxConstants.SYNTAX_STYLE_LESS;
            case "lisp":
            case "lsp":
            case "cl":
                return SyntaxConstants.SYNTAX_STYLE_LISP;
            case "lua":
                return SyntaxConstants.SYNTAX_STYLE_LUA;
            case "md":
            case "markdown":
                return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
            case "mxml":
                return SyntaxConstants.SYNTAX_STYLE_MXML;
            case "plx":
            case "pls":
            case "pl":
            case "pm":
            case "xs":
            case "t":
            case "pod":
            case "psgi":
                return SyntaxConstants.SYNTAX_STYLE_PERL;
            case "php":
            case "phar":
            case "pht":
            case "phtml":
            case "phs":
                return SyntaxConstants.SYNTAX_STYLE_PHP;
            case "py":
            case "pyw":
            case "pyz":
            case "pyi":
            case "pyc":
            case "pyd":
                return SyntaxConstants.SYNTAX_STYLE_PYTHON;
            case "rb":
            case "ru":
                return SyntaxConstants.SYNTAX_STYLE_RUBY;
            case "rs":
            case "rlib":
                return SyntaxConstants.SYNTAX_STYLE_RUST;
            case "sas":
                return SyntaxConstants.SYNTAX_STYLE_SAS;
            case "scala":
            case "sc":
                return SyntaxConstants.SYNTAX_STYLE_SCALA;
            case "sql":
            case "db":
                return SyntaxConstants.SYNTAX_STYLE_SQL;
            case "ts":
            case "tsx":
            case "mts":
            case "cts":
                return SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;
            case "sh":
                return SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            case "bat":
                return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
            case "vb":
                return SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC;
            case "xml":
                return SyntaxConstants.SYNTAX_STYLE_XML;
            case "yml":
            case "yaml":
            case "yarn": // .yarn's syntax looks like YAML syntax.
                return SyntaxConstants.SYNTAX_STYLE_YAML;
            default:
                return SyntaxConstants.SYNTAX_STYLE_NONE;
        }
    }

    public static String getLanguageIconNameByFile(File file) {
        String fileName = file.getName();

        switch (fileName.toLowerCase()) {
            case ".git":
            case ".gitignore":
                return "git.svg";
            case "dockerfile":
                return "docker.svg";
            case ".npmignore":
                return "npm.svg";
            case "package-lock.json":
            case "package.json":
                return "nodejs.svg";
            default:
                String extension = FilenameUtils.getExtension(file.getPath());

                switch (extension) {
                    case "as":
                        return "actionscript.svg";
                    case "asm":
                    case "s":
                    case "inc":
                        return "assembly.svg";
                    case "c":
                        return "c.svg";
                    case "clj":
                    case "cljs":
                    case "cljc":
                        return "clojure.svg";
                    case "cpp":
                        return "cpp.svg";
                    case "cs":
                        return "csharp.svg";
                    case "css":
                        return "css.svg";
                    case "d":
                        return "d.svg";
                    case "dart":
                        return "dart.svg";
                    case "f90":
                    case "for":
                    case "f":
                        return "fortran.svg";
                    case "go":
                        return "go.svg";
                    case "groovy":
                        return "groovy.svg";
                    case "htm":
                    case "html":
                        return "html.svg";
                    case "ini":
                    case "properties":
                    case "prop":
                    case "config":
                        return "settings.svg";
                    case "java":
                        return "java.svg";
                    case "class":
                        return "javaclass.svg";
                    case "jar":
                        return "jar.svg";
                    case "js":
                    case "mjs":
                    case "cjs":
                        return "javascript.svg";
                    case "json":
                    case "jsonc":
                        return "json.svg";
                    case "kt":
                        return "kotlin.svg";
                    case "tex":
                        return "tex.svg";
                    case "less":
                        return "less.svg";
                    case "lisp":
                    case "lsp":
                    case "cl":
                        return "lisp.svg";
                    case "lua":
                        return "lua.svg";
                    case "md":
                    case "markdown":
                        return "markdown.svg";
                    case "mxml":
                        return "mxml.svg";
                    case "plx":
                    case "pls":
                    case "pl":
                    case "pm":
                    case "xs":
                    case "t":
                    case "pod":
                    case "psgi":
                        return "perl.svg";
                    case "php":
                    case "phar":
                    case "pht":
                    case "phtml":
                    case "phs":
                        return "php.svg";
                    case "py":
                    case "pyw":
                    case "pyz":
                    case "pyi":
                    case "pyc":
                    case "pyd":
                        return "python.svg";
                    case "rb":
                    case "ru":
                        return "ruby.svg";
                    case "rs":
                    case "rlib":
                        return "rust.svg";
                    case "sas":
                        return "sas.svg";
                    case "scala":
                    case "sc":
                        return "scala.svg";
                    case "sql":
                    case "db":
                        return "database.svg";
                    case "ts":
                    case "tsx":
                    case "mts":
                    case "cts":
                        return "typescript.svg";
                    case "sh":
                    case "bat":
                        return "console.svg";
                    case "vb":
                        return "visualstudio.svg";
                    case "xml":
                        return "xml.svg";
                    case "yml":
                    case "yaml":
                        return "yaml.svg";
                    case "env":
                        return "tune.svg";
                    case "dat":
                    case "bin":
                    case "hex":
                        return "key.svg";
                    case "h":
                        return "h.svg";
                    case "hpp":
                        return "hpp.svg";
                    case "log":
                        return "log.svg";
                    case "zip":
                    case "rar":
                        return "zip.svg";
                    case "exe":
                        return "exe.svg";
                    case "ejs":
                        return "ejs.svg";
                    case "png":
                    case "jpeg":
                    case "jpg":
                    case "gif":
                    case "tiff":
                        return "image.svg";
                    case "mp4":
                    case "mov":
                    case "wmv":
                    case "webm":
                    case "3gp":
                        return "video.svg";
                    case "wav":
                    case "mp3":
                    case "ogg":
                        return "audio.svg";
                    case "pdf":
                        return "pdf.svg";
                    case "yarn":
                        return "yarn.svg";
                    default:
                        return "document.svg";
                }
        }
    }

    public static String getLanguageByFileExtension(String extension) {
        if (extension == null) {
            return "Plain Text";
        }

        switch (extension.toLowerCase()) {
            case "as":
                return "ActionScript";
            case "asm":
            case "s":
            case "inc":
                return "Assembly";
            case "c":
                return "The C Programming Language";
            case "clj":
            case "cljs":
            case "cljc":
                return "Clojure";
            case "cpp":
                return "C++";
            case "cs":
                return "C#";
            case "css":
                return "CSS";
            case "d":
                return "D";
            case "dart":
                return "Dart";
            case "f90":
            case "for":
            case "f":
                return "Fortran";
            case "go":
                return "Golang";
            case "groovy":
                return "Groovy";
            case "htm":
            case "html":
            case "ejs":
                return "HTML";
            case "ini":
            case "properties":
            case "prop":
            case "config":
                return "Properties";
            case "java":
                return "Java";
            case "js":
            case "mjs":
            case "cjs":
                return "JavaScript";
            case "json":
                return "JSON";
            case "jsonc":
                return "JSON with Comments";
            case "kt":
                return "Kotlin";
            case "tex":
                return "LaTeX";
            case "less":
                return "Less";
            case "lisp":
            case "lsp":
            case "cl":
                return "Lisp";
            case "lua":
                return "Lua";
            case "md":
            case "markdown":
                return "Markdown";
            case "mxml":
                return "MXML";
            case "plx":
            case "pls":
            case "pl":
            case "pm":
            case "xs":
            case "t":
            case "pod":
            case "psgi":
                return "The Perl Programming Language";
            case "php":
            case "phar":
            case "pht":
            case "phtml":
            case "phs":
                return "PHP";
            case "py":
            case "pyw":
            case "pyz":
            case "pyi":
            case "pyc":
            case "pyd":
                return "Python";
            case "rb":
            case "ru":
                return "Ruby";
            case "rs":
            case "rlib":
                return "Rust";
            case "sas":
                return "SAS Language";
            case "scala":
            case "sc":
                return "Scala";
            case "sql":
            case "db":
                return "SQL";
            case "ts":
            case "tsx":
            case "mts":
            case "cts":
                return "TypeScript";
            case "sh":
                return "Unix Shell";
            case "bat":
                return "Windows Batch";
            case "vb":
                return "Visual Basic";
            case "xml":
                return "XML";
            case "yml":
            case "yaml":
                return "YAML";
            case "txt":
                return "Text File";
            default:
                return "Plain Text";
        }
    }

    public static List<String> getSupportedAndEditableExtensions() {
        List<String> extensions = new ArrayList<String>();
        String[] supportedExtensions = {
                "as",
                "asm",
                "s",
                "inc",
                "c",
                "clj",
                "cljs",
                "cljc",
                "cpp",
                "cs",
                "css",
                "d",
                "dart",
                "f90",
                "for",
                "f",
                "go",
                "groovy",
                "htm",
                "html",
                "ejs",
                "ini",
                "properties",
                "prop",
                "config",
                "java",
                "js",
                "mjs",
                "cjs",
                "json",
                "jsonc",
                "kl",
                "kt",
                "lex",
                "less",
                "lisp",
                "lsp",
                "cl",
                "lua",
                "md",
                "markdown",
                "mxml",
                "plx",
                "pls",
                "pl",
                "pm",
                "xs",
                "t",
                "pod",
                "psgi",
                "php",
                "phar",
                "pht",
                "phtml",
                "phs",
                "py",
                "pyw",
                "rb",
                "ru",
                "rs",
                "rlib",
                "sas",
                "scala",
                "sc",
                "ts",
                "tsx",
                "mts",
                "cts",
                "sh",
                "bat",
                "vb",
                "xml",
                "yml",
                "yaml",
                "log",
                "txt",
                "env",
                "gitignore",
                "git",
                "npmignore",
                "yarn"
        };

        for (String extension : supportedExtensions) {
            extensions.add(extension);
        }

        return extensions;
    }

    public static void setTextSyntaxHighlightingColors(RSyntaxTextArea textArea) {
        SyntaxScheme scheme = textArea.getSyntaxScheme();

        scheme.getStyle(Token.ANNOTATION).foreground = Color.decode("#008000");
        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.decode("#0037a4");
        scheme.getStyle(Token.RESERVED_WORD_2).foreground = Color.decode("#0037a4");

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#008000");
        scheme.getStyle(Token.LITERAL_CHAR).foreground = Color.decode("#008000");
        scheme.getStyle(Token.LITERAL_BACKQUOTE).foreground = Color.decode("#008000");

        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = Color.decode("#1750ea");

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = Color.BLUE;
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = Color.BLUE;
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = Color.BLUE;

        scheme.getStyle(Token.REGEX).foreground = Color.decode("#cb1823");

        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = Color.GRAY;
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = Color.GRAY;
        scheme.getStyle(Token.COMMENT_EOL).foreground = Color.GRAY;

        scheme.getStyle(Token.SEPARATOR).foreground = Color.BLACK;
        scheme.getStyle(Token.OPERATOR).foreground = Color.BLACK;
        scheme.getStyle(Token.IDENTIFIER).foreground = Color.BLACK;
        scheme.getStyle(Token.VARIABLE).foreground = Color.decode("#c02d2e");
        scheme.getStyle(Token.FUNCTION).foreground = Color.RED;
        scheme.getStyle(Token.PREPROCESSOR).foreground = Color.decode("#0037a4");

        // HTML / XML related
        scheme.getStyle(Token.MARKUP_CDATA).foreground = Color.decode("#0037a4");
        scheme.getStyle(Token.MARKUP_COMMENT).foreground = Color.GRAY;
        scheme.getStyle(Token.MARKUP_DTD).foreground = Color.decode("#bc8c2b");
        // scheme.getStyle(Token.MARKUP_ENTITY_REFERENCE).foreground = Color.BLUE;
        // scheme.getStyle(Token.MARKUP_PROCESSING_INSTRUCTION).foreground = Color.BLUE;
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE).foreground = Color.decode("#64278f");
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = Color.decode("#008000");
        scheme.getStyle(Token.MARKUP_TAG_DELIMITER).foreground = Color.decode("#1c1a5d");
        scheme.getStyle(Token.MARKUP_TAG_NAME).foreground = Color.decode("#1c1a5d");

        textArea.setSyntaxScheme(scheme);
    }

    public static String getAboutScriptor() {
        String[] array = {
            "General information:",
            "• Version: " + getVersion(),
            "• Author(s): TFAGaming",
            "• License: The MIT License",
            "",
            "Dependencies used to make Scriptor:",
            "• Apache Commons IO",
            "• RSyntaxTextArea (com.fifesoft)",
            "• Autocomplete (com.fifesoft)",
            "• Batik Codec (org.apache.xmlgraphics)",
            "• Jackson Databind (com.fasterxml.jackson.core)"
        };
        String finalString = "";

        for (String each : array) {
            finalString += each + "\n";
        }

        return finalString;
    }
}
