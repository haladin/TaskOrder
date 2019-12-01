package com.example.data

import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "command", "requires")
@JsonIgnoreProperties(value= ["requires"], allowSetters=true)
class Task {
    @get:JsonProperty("name")
    @set:JsonProperty("name")
    @JsonProperty("name")
    var name: String? = null
    @get:JsonProperty("command")
    @set:JsonProperty("command")
    @JsonProperty("command")
    var command: String? = null
    @get:JsonProperty("requires")
    @set:JsonProperty("requires")
    @JsonProperty("requires")
    var requires: List<String>? = null
    @JsonIgnore
    private val additionalProperties: MutableMap<String, Any> =
        HashMap()

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}