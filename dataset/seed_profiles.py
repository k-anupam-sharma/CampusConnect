import os
import json
import asyncio
from supabase import create_client, Client

# Use the credentials from your .env
url: str = "https://ztsgghwtdtxxopxaqnwx.supabase.co"
key: str = "sb_publishable_nmRPk6YXjBRhYuViLb1J7Q_OR5q20sO"
supabase: Client = create_client(url, key)

def seed_profiles():
    # Load Students
    with open("Students.json", "r", encoding="utf-8") as f:
        students = json.load(f)
        
    for student in students:
        profile_data = {
            "name": student.get("name"),
            "role": "student",
            "department": student.get("department"),
            "year": student.get("year"),
            "skills": student.get("skills"),
            "projects": student.get("projects"),
            "research_interests": student.get("research_interests"),
            "certifications": student.get("certifications"),
            "career_interests": student.get("career_interests")
        }
        try:
            supabase.table("profiles").insert(profile_data).execute()
            print(f"Inserted student: {student.get('name')}")
        except Exception as e:
            print(f"Failed to insert {student.get('name')}: {e}")

    # Load Faculty
    with open("Faculty.json", "r", encoding="utf-8") as f:
        faculty_list = json.load(f)
        
    for faculty in faculty_list:
        profile_data = {
            "name": faculty.get("name"),
            "role": "faculty",
            "department": faculty.get("department"),
            "research_interests": faculty.get("research_interests"),
            "expertise": faculty.get("expertise"),
            "publications": faculty.get("publications")
        }
        try:
            supabase.table("profiles").insert(profile_data).execute()
            print(f"Inserted faculty: {faculty.get('name')}")
        except Exception as e:
            print(f"Failed to insert {faculty.get('name')}: {e}")

if __name__ == "__main__":
    print("Starting seeding process...")
    seed_profiles()
    print("Done!")
