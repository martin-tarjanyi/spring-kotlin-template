package com.example.product.dataaccess.http.rickandmorty

import com.example.rickmorty.generated.client.CharacterGraphQLQuery
import com.example.rickmorty.generated.client.CharacterProjectionRoot
import com.example.rickmorty.generated.types.Character
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import kotlinx.coroutines.reactor.awaitSingle

class RickAndMortyGraphQlClient(
    private val client: MonoGraphQLClient,
) {
    suspend fun findCharacterById(id: String): Character {
        val query = CharacterGraphQLQuery
            .newRequest()
            .id(id)
            .build()

        val projection = CharacterProjectionRoot<Nothing, Nothing>()
            .id()
            .name()
            .apply { episode().name().id() }

        val graphQlQuery = GraphQLQueryRequest(query, projection)
        val response = client.reactiveExecuteQuery(graphQlQuery.serialize()).awaitSingle()
        return response.extractValueAsObject(query.getOperationName(), Character::class.java)
    }
}
