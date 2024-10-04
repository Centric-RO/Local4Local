import ChipData from "@/models/chip-data";
import { fireEvent, render } from "@testing-library/react-native";
import ChipSelector from "@/components/chip-selector/chip-selector";

const mockedChipData: ChipData[] = [
	{
		id: 0,
		label: 'chip-0'
	},
	{
		id: 1,
		label: 'chip-1'
	}
];

const mockOnSelect = jest.fn();

jest.mock('react-i18next', () => {
	return {
		useTranslation: () => ({
			t: jest.fn((key) => key),
		})
	};
});

describe('<ChipSelector />', () => {
	it('should render the chips when there is data', () => {
		const {getByText} = render(
			<ChipSelector
				data={mockedChipData}
				selectedId={mockedChipData[0].id}
				onSelect={mockOnSelect} />
		);

		expect(getByText('chip-0')).toBeTruthy();
	});

	it('should update the selected chip when pressing a chip', () => {
		const {getByText} = render(
			<ChipSelector
				data={mockedChipData}
				selectedId={mockedChipData[0].id}
				onSelect={mockOnSelect} />
		);

		fireEvent(getByText('chip-1'), 'press');

		expect(mockOnSelect).toHaveBeenCalledTimes(1);
	});
});