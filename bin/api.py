from __future__ import annotations

import boto3
import datetime
import json
import requests as requests
from typing import NamedTuple


class CdkOutputs(NamedTuple):
    api_url: str
    pool_url: str
    client_id: str
    pool_id: str

    @classmethod
    def load(cls) -> CdkOutputs:
        outputs = json.loads("..//core//cdk-outputs.json")["DevStack"]
        return CdkOutputs(
            api_url=outputs["apiUrl"],
            pool_url=outputs["userPoolBaseUrl"],
            client_id=outputs["userPoolClientId"],
            pool_id=outputs["userPoolId"])


class Api:
    __slots__ = (
        "config",
        "username",
        "password",
        "id_token",
        "refresh_token",
        "renewed_at",
        "tokens_ttl",
        "_cognito")

    def __init__(self, username: str, password: str):
        self.config: CdkOutputs = CdkOutputs.load()
        self.username = username
        self.password = password
        self.id_token = ""
        self.refresh_token = ""
        self.renewed_at = datetime.datetime.min
        self.tokens_ttl = datetime.timedelta.min
        self._cognito = boto3.client("cognito-idp")
        self.renew_tokens()

    def get_upload_url(self):
        self.renew_tokens()
        response = requests.get(
            url=self.config.api_url + "//code",
            headers={"Content-Type": "application/json"},
            data={})
        result = None
        if response.ok:
            data = response.json()
            result = (data["uploadUrl"], data["codeId"])
        return result

    @staticmethod
    def upload_code(upload_url: str, path: str):
        with open(path, "rb") as code:
            requests.put(url=upload_url, data=code)

    def renew_tokens(self):
        if self._should_renew():
            try:
                self._renew_with_refresh_token()
            except Exception:
                self._renew_tokens()

    def _should_renew(self):
        return (datetime.datetime.now() - self.renewed_at) > self.tokens_ttl

    def _renew_with_refresh_token(self):
        response = self._cognito.initiate_auth(
            UserPoolId=self.config.pool_id,
            ClientId=self.config.client_id,
            AuthFlow="REFRESH_TOKEN_AUTH",
            AuthParameters={"REFRESH_TOKEN": self.refresh_token})
        result = response["AuthenticationResult"]
        self.id_token = result["IdToken"]
        self.renewed_at = datetime.datetime.now()
        self.tokens_ttl = datetime.timedelta(seconds=result["ExpiresIn"])

    def _renew_tokens(self):
        response = self._cognito.initiate_auth(
            UserPoolId=self.config.pool_id,
            ClientId=self.config.client_id,
            AuthFlow="USER_PASSWORD_AUTH",
            AuthParameters={
                "USERNAME": self.username, "PASSWORD": self.password})
        result = response["AuthenticationResult"]
        self.id_token = result["IdToken"]
        self.refresh_token = result["RefreshToken"]
        self.renewed_at = datetime.datetime.now()
        self.tokens_ttl = datetime.timedelta(seconds=result["ExpiresIn"])
