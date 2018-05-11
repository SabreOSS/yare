<!--
  MIT License

  Copyright 2018 Sabre GLBL Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 -->

## _YARE_ - Rules Engine in Plain Java

[![Build Status](https://travis-ci.org/SabreOSS/yare.svg?branch=master)](https://travis-ci.org/SabreOSS/yare)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sabre.oss.yare/yare/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sabre.oss.yare/yare)

**YARE** (**Y**et **A**nother **R**ules **E**ngine) is a rule engine written in Java.
It is an approach to implement an Expert System which is reliable, fast and full of capabilities.


#### Why Yet Another Rules Engine?

Beyond the advantages of business rules engines YARE makes its own contribution to user experience:

* YARE is significantly faster than other rules engines for single-type collection input (e.q. List\<Flight\>).
* YARE allows for sequential evaluation, which is useful when it comes to modifying facts during execution.
* YARE allows for function evaluation in condition segment of rule.
* YARE is using three-valued logic (true/false/null).
* YARE provides a rule converter for XML.

## Getting Started

To start playing with _YARE_ just add the dependency to _com.sabre.oss.yare:yare-engine_:

_Maven_
```xml
<dependency>
    <groupId>com.sabre.oss.yare</groupId>
    <artifactId>yare-engine</artifactId>
    <version>${yare.version}</version>
</dependency>
```

_Gradle_
```groovy
dependencies {
  compile "com.sabre.oss.yare:yare-engine:$yareVersion"
}
```

## Example

First step towards evaluation using the rule engine is to define a rule.

We can do it either using Java DSL:
```java
Rule rule = RuleDsl.ruleBuilder()
                .name("Should match flight with first class of service")
                .fact("flightFact", Flight.class)
                .predicate(
                        equal(
                                value("${flightFact.classOfService}"),
                                value(classOfService)
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${flightFact}")))
                .build();
```

or using XML:
```xml
<Rule xmlns="http://www.sabre.com/schema/oss/yare/rules/v1">
    <Attribute name="ruleName" value="Should match flight with first class of service" type="java.lang.String"/>
    <Fact name="flightFact" type="com.sabre.oss.yare.example.Flight"/>
    <Predicate>
        <Operator type="equal">
            <Value>${flight.classOfService}</Value>
            <Value type="java.lang.String">First Class</Value>
        </Operator>
    </Predicate>
    <Action name="collect">
        <Parameter name="context">
            <Value>${ctx}</Value>
        </Parameter>
        <Parameter name="fact">
            <Value>${flightFact"</Value>
        </Parameter>
    </Action>
</Rule>
```

As you can see we operate on `Flight` fact so let's define it too.
```java
public class Flight {
        private String destination;
        private String classOfService;

        // getters and setters
}
```

Since we are using `collect` action we have to provide implementation as well.
```java
public class Action {
        public void collect(List<Flight> result, Flight fact) {
            result.add(fact);
        }
}
```

Last, but certainly not least, we need to create the rules engine itself.
Exactly as shown below.
```java
RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository((uri) -> Collections.singletonList(rule))
                .withActionMapping("collect", method(new Action(), (action) -> action.collect(null, null)))
                .build()
```

The only thing left is to evaluate our example.
```java
RuleSession ruleSession = rulesEngine.createSession("ruleSet");
List<Flight> result = ruleSession.execute(new ArrayList(), flights);
```

For more information about how to use YARE please read [YARE User's Guide](https://github.com/SabreOSS/yare/wiki)
and check yare-examples module.

## Contributing

We accept pull request via _GitHub_. Here are some guidelines which will make applying PRs easier for us:

* No tabs. Please use spaces for indentation.
* Respect the code style.
* Create minimal diffs - disable on save actions like reformat source code or organize imports.
  If you feel the source code should be reformatted create a separate PR for this change.
* Provide _JUnit_ tests for your changes and make sure they don't break anything by running
  `mvn clean verify`.

See [CONTRIBUTING](CONTRIBUTING.md) document for more details.

## License

Copyright 2018 Sabre GLBL Inc.

Code is under the [MIT license](LICENSE).
