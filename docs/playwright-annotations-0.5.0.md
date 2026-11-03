[← Back to README](../README.md)
## Playwright locating annotations


Besides `@Sensor` with a CSS/XPath string, Playwright pages can use Playwright's own locators.
Each annotation calls the matching `page.getBy...(...)` method:

| Annotation | Playwright call | Options (attributes) |
|---|---|---|
| `@ByRole(role = ...)` | `getByRole(role, options)` | `value` = accessible name, `exact`, `regex`, `level`, `checked`, `disabled`, `expanded`, `pressed`, `selected`, `includeHidden` |
| `@ByText` | `getByText(...)` | `exact`, `regex` |
| `@ByLabel` | `getByLabel(...)` | `exact`, `regex` |
| `@ByPlaceholder` | `getByPlaceholder(...)` | `exact`, `regex` |
| `@ByAltText` | `getByAltText(...)` | `exact`, `regex` |
| `@ByTitle` | `getByTitle(...)` | `exact`, `regex` |
| `@ByTestId` | `getByTestId(...)` | `regex` |
| `@ByAttribute(name = ...)` | `locator("[name='value']")` | `exact` (`false` = contains) |

```java
@Telemetry("pages/login-page.telemetry")
public interface LoginPage {

    @ByLabel                                         // key "emailInput" in the file
    LocatorSupplier emailInput();

    @ByLabel(value = "$Password", exact = true)      // literal value
    LocatorSupplier passwordInput();

    @ByRole(role = AriaRole.BUTTON, value = "sign-in-button", description = "Sign In button")
    LocatorSupplier signInButton();

    @ByRole(role = AriaRole.HEADING, value = "$Welcome back", level = 1)
    LocatorSupplier pageHeading();

    @ByRole(role = AriaRole.CHECKBOX, value = "$Remember me", checked = Flag.TRUE)
    LocatorSupplier rememberMeChecked();

    @ByText(value = "$Welcome, {user}", exact = true)
    LocatorSupplier welcomeMessage(@Param("user") String user);

    @ByRole(role = AriaRole.LISTITEM)                // no name: all list items
    LocatorSupplier hints(PostfixLocator postfix);
}
```

Rules:
- `value` works like in `@Sensor`: `""` = the method name is the key, `"$..."` = literal, anything else = a key in the
  .telemetry file. `{param}` placeholders and stages (`%prod.key=...`) work the same way.
- For `@ByRole`, `""` means **no name** (search by role only).
- Options use Playwright's defaults, so you only write what differs. `Flag.ANY` means "not set".
- `regex = true` treats the value as a regular expression.
- `description` is the readable name returned by `getVariableName()` (default: the method name).
- Use exactly one locating annotation per method. These annotations work on Playwright pages only.
- Requires Playwright 1.55 or newer.
