from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import psycopg2
from psycopg2.extras import RealDictCursor
import time
from contextlib import contextmanager
from typing import List, Optional

app = FastAPI()

# Database connection configuration
DB_CONFIG = {
    "host": "db",
    "port": 5432,
    "database": "postgres",
    "user": "postgres",
    "password": "postgres"
}

@contextmanager
def get_db_connection():
    """데이터베이스 연결을 관리하는 context manager"""
    conn = None
    try:
        conn = psycopg2.connect(**DB_CONFIG, cursor_factory=RealDictCursor)
        yield conn
    finally:
        if conn:
            conn.close()

def init_db():
    """데이터베이스 초기화 및 샘플 데이터 생성"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                # 테이블 생성
                cur.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                # 샘플 데이터 삽입 (이미 존재하는지 확인)
                cur.execute("SELECT COUNT(*) as count FROM users")
                if cur.fetchone()['count'] == 0:
                    cur.execute("""
                        INSERT INTO users (name, email) VALUES
                        ('홍길동', 'hong@example.com'),
                        ('김철수', 'kim@example.com'),
                        ('이영희', 'lee@example.com')
                    """)
                
                conn.commit()
                print("Database initialized successfully")
    except Exception as e:
        print(f"Database initialization error: {e}")

class User(BaseModel):
    id: Optional[int] = None
    name: str
    email: str

@app.on_event("startup")
async def startup_event():
    """애플리케이션 시작 시 실행"""
    print("Backend starting...")
    time.sleep(5)  # 일부러 느리게 시작 (depends_on 테스트용)
    init_db()

@app.get("/")
def root():
    """루트 엔드포인트"""
    return {"message": "Hello from FastAPI backend"}

@app.get("/health")
def health():
    """헬스체크 엔드포인트"""
    return {"status": "OK"}

@app.get("/users", response_model=List[dict])
def get_users():
    """모든 사용자 조회"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                cur.execute("SELECT id, name, email, created_at FROM users ORDER BY id")
                users = cur.fetchall()
                return users
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")

@app.get("/users/{user_id}")
def get_user(user_id: int):
    """특정 사용자 조회"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                cur.execute("SELECT id, name, email, created_at FROM users WHERE id = %s", (user_id,))
                user = cur.fetchone()
                if user is None:
                    raise HTTPException(status_code=404, detail="User not found")
                return user
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")

@app.post("/users", response_model=dict)
def create_user(user: User):
    """새 사용자 생성"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                cur.execute(
                    "INSERT INTO users (name, email) VALUES (%s, %s) RETURNING id, name, email, created_at",
                    (user.name, user.email)
                )
                new_user = cur.fetchone()
                conn.commit()
                return new_user
    except psycopg2.IntegrityError:
        raise HTTPException(status_code=400, detail="Email already exists")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")

@app.put("/users/{user_id}", response_model=dict)
def update_user(user_id: int, user: User):
    """사용자 정보 수정"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE users SET name=%s, email=%s WHERE id=%s RETURNING id, name, email, created_at",
                    (user.name, user.email, user_id)
                )
                updated = cur.fetchone()
                if updated is None:
                    raise HTTPException(status_code=404, detail="User not found")
                conn.commit()
                return updated
    except HTTPException:
        raise
    except psycopg2.IntegrityError:
        raise HTTPException(status_code=400, detail="Email already exists")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")

@app.delete("/users/{user_id}")
def delete_user(user_id: int):
    """사용자 삭제"""
    try:
        with get_db_connection() as conn:
            with conn.cursor() as cur:
                cur.execute("DELETE FROM users WHERE id=%s RETURNING id", (user_id,))
                deleted = cur.fetchone()
                if deleted is None:
                    raise HTTPException(status_code=404, detail="User not found")
                conn.commit()
                return {"message": f"User {user_id} deleted"}
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
