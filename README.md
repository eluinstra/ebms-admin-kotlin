# ebms-admin-kotlin

## Issues
- Grid refresh not working
- i18n (getTranslation()) params printed as pointers ([Ljava.lang.Object;@17b51763) instead of string ("test")
  - solution: provide a Java TranslationProvider instead of a Kotlin one
- separator looks not nice when defined on menuItems directly
  - workaround: add it as a separate disabled menuitem: `item(hr()).isEnabled = false`
- Material theme:
  - separator is not displayed properly
  - notification lumo (color) variants are not working with material