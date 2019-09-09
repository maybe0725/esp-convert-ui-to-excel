# Create Eclipse SWT Project Example

<br/>

참고

- https://bitsoul.tistory.com/97
- https://www.eclipse.org/windowbuilder/download.php
- https://www.eclipse.org/swt/snippets/
- https://www.vogella.com/tutorials/EclipseDialogs/article.html
- https://www.vogella.com/tutorials/SWT/article.html#swt_overview

<br/>

## 1. Eclipse SWT Installing WindowBuilder Pro

<br/>

## 2. Create Eclipse a plug-in project

`Step 1.` File > New > Project... > Plug-in Development > Plug-in Project

`Step 2.` Content > Options > uncheck

`Step 3.` 'MANIFEST.MF' File Open > Dependencies > Add... > Select a Plug-in

- 'org.eclipse.swt' Add
- 'org.eclipse.core.runtime' Add
- 'org.eclipse.jface' Add

<br/>

## 3. Add SWT

Project > New > Other... > Search Wizards: 'SWT'

Select a wizard : 'Application Window', 'Composite', 'Dialog', 'Shell' ... or 'JFace'...

Select a SWT Java File > Opne With > WindowBuilder Editor

<br/>

## 4. Enjoy SWT Programming.

<br/>

## 5. Add Build Path jar files list

/esp-convert-ui-to-excel/lib

- commons-collections4-4.3.jar
- commons-compress-1.18.jar
- commons-lang3-3.9.jar
- gson-parent-2.6.2.jar
- poi-4.1.0.jar
- poi-ooxml-4.1.0.jar
- poi-ooxml-schemas-4.1.0.jar
- xmlbeans-3.1.0.jar
