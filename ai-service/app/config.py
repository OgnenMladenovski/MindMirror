"""Runtime configuration for the MindMirror AI microservice."""
from __future__ import annotations

from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env", extra="ignore", protected_namespaces=("settings_",)
    )

    app_name: str = "MindMirror AI"
    version: str = "0.1.0"

    # Chat backend: "rule_based" (default, offline) or "openai" (requires OPENAI_API_KEY).
    chat_backend: str = "rule_based"
    openai_api_key: str | None = None
    openai_model: str = "gpt-4o-mini"

    # Where trained models are persisted.
    model_dir: Path = Path(__file__).resolve().parent.parent / "model_store"


settings = Settings()
