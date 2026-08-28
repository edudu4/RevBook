import { useState, useRef, useEffect } from 'react';
import { Smile } from 'lucide-react';

interface ReactionPickerProps {
  onSelecionarEmoji: (emoji: string) => void;
  isOpen: boolean;
  onClose: () => void;
}

const EMOJIS_DISPONIVEIS = ['👍', '❤️', '😂', '😮', '😢', '😡', '🔥', '🎉', '💯'];

export default function ReactionPicker({ onSelecionarEmoji, isOpen, onClose }: ReactionPickerProps) {
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const pickerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (pickerRef.current && !pickerRef.current.contains(event.target as Node)) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      ref={pickerRef}
      className="absolute bottom-full mb-2 left-0 bg-card border border-border rounded-lg shadow-lg p-2 z-50 max-w-[calc(100vw-2rem)] overflow-x-auto"
    >
      <div className="flex flex-nowrap gap-1">
        {EMOJIS_DISPONIVEIS.map((emoji) => (
          <button
            key={emoji}
            onClick={() => {
              onSelecionarEmoji(emoji);
              onClose();
            }}
            className="text-xl hover:scale-125 transition-transform cursor-pointer p-1 flex-shrink-0"
            title={emoji}
          >
            {emoji}
          </button>
        ))}
      </div>
    </div>
  );
}
