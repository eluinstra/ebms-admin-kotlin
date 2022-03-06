# ebms-admin-kotlin

## Todo:
- details/edit inline in grid or in a dialog instead of on separate detail pages
- grid functions per row in context menu (available on right click)
- custom error page
- admin properties page

## Issues
- Grid refresh not working
- No event.stopPropagation() available in Java, so cannot combine a grid with clickable item details and buttons
  - workaround move buttons to context menu
- i18n (getTranslation()) params printed as pointers ([Ljava.lang.Object;@17b51763) instead of string ("test") with Kotlin TranslationProvider
  - solution: provide a Java TranslationProvider instead 
- Separator does not look nice when defined on menuItems directly in Karibu. This does not behave the same as in Vaadin.
  - workaround: add it as a separate disabled menuitem: `item(hr()).isEnabled = false`
- Material theme:
  - separator is not displayed properly
  - notification lumo (color) variants are not working with material