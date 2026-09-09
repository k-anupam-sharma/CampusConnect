-- Create the messages table
CREATE TABLE messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    connection_id TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL DEFAULT '',
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Enable Row Level Security
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- Create policy to allow all users (Anon) to select messages
CREATE POLICY "Allow anonymous read" ON messages
    FOR SELECT
    TO anon
    USING (true);

-- Create policy to allow all users (Anon) to insert messages
CREATE POLICY "Allow anonymous insert" ON messages
    FOR INSERT
    TO anon
    WITH CHECK (true);

-- Enable Realtime for the messages table
BEGIN;
  DROP PUBLICATION IF EXISTS supabase_realtime;
  CREATE PUBLICATION supabase_realtime;
COMMIT;
ALTER PUBLICATION supabase_realtime ADD TABLE messages;

-- Create the profiles table
CREATE TABLE profiles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    role TEXT NOT NULL,
    department TEXT,
    year TEXT,
    skills TEXT,
    projects TEXT,
    research_interests TEXT,
    certifications TEXT,
    career_interests TEXT,
    expertise TEXT,
    publications TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- RLS policies for profiles
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow anonymous read profiles" ON profiles FOR SELECT TO anon USING (true);
CREATE POLICY "Allow anonymous insert profiles" ON profiles FOR INSERT TO anon WITH CHECK (true);

ALTER PUBLICATION supabase_realtime ADD TABLE profiles;
