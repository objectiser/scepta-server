
drop keyspace scepta;

create keyspace scepta with replication = {'class': 'SimpleStrategy','replication_factor': 1};

use scepta;

create table organizations (
    organization text,
    data text,
    PRIMARY KEY (organization)
);

create table policygroups (
    organization text,
    group text,
    tag text,
    data text,
    PRIMARY KEY (organization, group, tag)
);

create table policies (
    organization text,
    group text,
    tag text,
    policy text,
    data text,
    PRIMARY KEY (organization, group, tag, policy)
);

create table policydefns (
    organization text,
    group text,
    tag text,
    policy text,
    data text,
    PRIMARY KEY (organization, group, tag, policy)
);

create table resourcedefns (
    organization text,
    group text,
    tag text,
    policy text,
    resource text,
    data text,
    PRIMARY KEY (organization, group, tag, policy, resource)
);


insert into scepta.organizations (organization, data)
    values ('Overlord', '{ "name":"Overlord", "description":"The Overlord organization" }');

insert into scepta.policygroups (organization, group, tag, data)
    values ('Overlord', 'RTGov', 'master', '{
    "description": "Runtime Governance policy group",
    "endpoints": [{
        "name": "activityunits",
        "actual": "activemq:queue:activityunits",
        "description": "Activity Units",
        "characteristics": [{
            "type": "Queue",
            "properties": {}
        },{
            "type": "BatchWithRetryOnFailure",
            "properties": {
                "batchSize": "500",
                "batchInterval": "1000",
                "maxRetry": "3"
            }
        }],
        "dependencies": [{
            "groupId": "org.overlord.rtgov.activity-management",
            "artifactId": "activity",
            "version": "2.1.0.Beta1"
        }],
        "consumerOptions": {
            "maxConcurrentConsumers": "30"
        },
        "producerOptions": {}
    },{
        "name": "servicedefns",
        "actual": "activemq:queue:servicedefns",
        "description": "Service Definitions derived from the Activity Units",
        "characteristics": [{
            "type": "Queue",
            "properties": {}
        },{
            "type": "BatchWithRetryOnFailure",
            "properties": {
                "batchSize": "500",
                "batchInterval": "1000",
                "maxRetry": "3"
            }
        }],
        "dependencies": [{
            "groupId": "org.overlord.rtgov.activity-analysis",
            "artifactId": "analytics",
            "version": "2.1.0.Beta1"
        }],
        "consumerOptions": {
            "maxConcurrentConsumers": "30"
        },
        "producerOptions": {}
    },{
        "name": "responsetimes",
        "actual": "activemq:queue:responsetimes",
        "description": "Response time information",
        "characteristics": [{
            "type": "Queue",
            "properties": {}
        },{
            "type": "BatchWithRetryOnFailure",
            "properties": {
                "batchSize": "500",
                "batchInterval": "1000",
                "maxRetry": "3"
            }
        }],
        "dependencies": [{
            "groupId": "org.overlord.rtgov.activity-analysis",
            "artifactId": "analytics",
            "version": "2.1.0.Beta1"
        }],
        "consumerOptions": {
            "maxConcurrentConsumers": "30"
        },
        "producerOptions": {}
    },{
        "name": "situations",
        "actual": "activemq:queue:situations",
        "description": "Reported Situations",
        "characteristics": [{
            "type": "Queue",
            "properties": {}
        },{
            "type": "BatchWithRetryOnFailure",
            "properties": {
                "batchSize": "500",
                "batchInterval": "1000",
                "maxRetry": "3"
            }
        }],
        "dependencies": [{
            "groupId": "org.overlord.rtgov.activity-analysis",
            "artifactId": "analytics",
            "version": "2.1.0.Beta1"
        }],
        "consumerOptions": {
            "maxConcurrentConsumers": "30"
        },
        "producerOptions": {}
    }],
    "name": "RTGov"
}');

insert into scepta.policies (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ActivityServer', '{
    "description": "Activity server for reporting activities related to runtime governance",
    "dependencies": [],
    "name": "ActivityServer",
    "resources": []
}');

insert into scepta.policydefns (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ActivityServer', '<?xml version="1.0" encoding="UTF-8"?>
<bean xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:camel="http://camel.apache.org/schema/spring"
  xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

  <camelContext xmlns="http://camel.apache.org/schema/spring">
    <restConfiguration component="servlet" bindingMode="json" contextPath="gov-next-example-activity-rest/rest" port="8080">
      <dataFormatProperty key="prettyPrint" value="true"/>
    </restConfiguration>

    <!-- defines the rest services using the context-path /user -->
    <rest path="/activities" consumes="application/json" produces="application/json">
      <description>Activities rest service</description>

      <put type="org.overlord.rtgov.activity.model.ActivityUnit" >
        <description>Store activity unit</description>
        <inOnly uri="scepta:activityunits"/>
      </put>
    </rest>

  </camelContext>

  <bean id="aggregatorStrategy" class="org.overlord.gov.examples.aggregator.ListAggregator"/>

</beans>
');

insert into scepta.policies (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ServiceDefinition', '{
    "description": "Convert activity units into a description of the activity that occurred within a service.",
    "dependencies": [{
        "groupId": "org.overlord.rtgov.examples",
        "artifactId": "servicedefnprocessor",
        "version": "something"
    }],
    "name": "ServiceDefinition",
    "resources": []
}');

insert into scepta.policydefns (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ServiceDefinition', '<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:camel="http://camel.apache.org/schema/spring"
       xsi:schemaLocation="
         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

  <camelContext xmlns="http://camel.apache.org/schema/spring">
	  <route id="servicedefn">
		  <from uri="scepta:activityunits"/>
          <to uri="serviceDefinitionProcessor"/> 
		  <inOnly uri="scepta:servicedefns" />
	  </route>
  </camelContext>

  <bean id="serviceDefinitionProcessor" class="org.overlord.gov.examples.processor.ServiceDefinitionProcessor"/>
</beans>');

insert into scepta.policies (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ResponseTime', '{
    "description": "Convert service definitions into response time information.",
    "dependencies": [{
        "groupId": "org.overlord.rtgov.examples",
        "artifactId": "responsetimeprocessor",
        "version": "something"
    }],
    "name": "ResponseTime",
    "resources": []
}');

insert into scepta.policydefns (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'ResponseTime', '<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:camel="http://camel.apache.org/schema/spring"
       xsi:schemaLocation="
         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

  <camelContext xmlns="http://camel.apache.org/schema/spring">

	  <route id="responsetime">
		  <from uri="activemq:queue:servicedefns?maxConcurrentConsumers=30"/>
          <to uri="responseTimeProcessor"/> 
		  <inOnly uri="activemq:queue:responsetimes" />
	  </route>

  </camelContext>

  <bean id="responseTimeProcessor" class="org.overlord.gov.examples.processor.ResponseTimeProcessor"/>

</beans>');

insert into scepta.policies (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'SLA', '{
    "description": "Convert activity units into a description of the activity that occurred within a service.",
    "dependencies": [],
    "name": "SLA",
    "resources": [{
        "name": "SLAViolation.mvel",
        "description": "Script that checks for SLA violations and creates a Situation when one is detected",
        "dependencies": []
    }]
}');

insert into scepta.policydefns (organization, group, tag, policy, data)
    values ('Overlord', 'RTGov', 'master', 'SLA', '<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:camel="http://camel.apache.org/schema/spring"
       xsi:schemaLocation="
         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

  <camelContext xmlns="http://camel.apache.org/schema/spring">
	  <route id="sla">
		  <from uri="scepta:responsetimes"/>
          <to uri="mvel:SLAViolation.mvel"/> 
		  <inOnly uri="scepta:situations" />
	  </route>
  </camelContext>
</beans>');

insert into scepta.resourcedefns (organization, group, tag, policy, resource, data)
    values ('Overlord', 'RTGov', 'master', 'SLA', 'SLAViolation.mvel', 'org.overlord.rtgov.analytics.situation.Situation sit=null;

if (body.average > 200) {
	sit = new org.overlord.rtgov.analytics.situation.Situation();
	
	sit.setType("SLA Violation");
	sit.setSubject(org.overlord.rtgov.analytics.situation.Situation.createSubject(body.getServiceType(),
					body.getOperation(), body.getFault()));
	sit.setTimestamp(java.lang.System.currentTimeMillis());
	
	sit.getProperties().putAll(body.getProperties());

	if (body.getRequestId() != null) {
		sit.getActivityTypeIds().add(body.getRequestId());
	}
	if (body.getResponseId() != null) {
		sit.getActivityTypeIds().add(body.getResponseId());
	}
	
	sit.getContext().addAll(body.getContext());
	
	String serviceName=body.getServiceType();
	
	if (serviceName.startsWith("{")) {
		serviceName = javax.xml.namespace.QName.valueOf(serviceName).getLocalPart();
	}
	
	sit.setDescription(serviceName+" exceeded maximum response time of 200 ms");
	sit.setSeverity(org.overlord.rtgov.analytics.situation.Situation.Severity.Low);
}
	
return sit;');


